package com.runanywhere.runanywhereai.presentation.disaster.damage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.models.RunAnywhereGenerationOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DamageUiState(
    val damageDescription: String = "",
    val location: String = "",
    val structureType: StructureType = StructureType.BUILDING,
    val isAnalyzing: Boolean = false,
    val error: String? = null
)

class DamageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DamageUiState())
    val uiState: StateFlow<DamageUiState> = _uiState.asStateFlow()

    private val _assessments = MutableStateFlow<List<DamageAssessment>>(emptyList())
    val assessments: StateFlow<List<DamageAssessment>> = _assessments.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(damageDescription = value, error = null)
    }

    fun updateLocation(value: String) {
        _uiState.value = _uiState.value.copy(location = value, error = null)
    }

    fun setStructureType(type: StructureType) {
        _uiState.value = _uiState.value.copy(structureType = type)
    }

    fun analyzeDamage() {
        val currentState = _uiState.value
        
        // Validation
        if (currentState.damageDescription.isBlank()) {
            _uiState.value = currentState.copy(error = "⚠️ Please describe the damage")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isAnalyzing = true, error = null)

                // Check if model is loaded
                val currentModel = RunAnywhere.currentModel
                if (currentModel == null) {
                    _uiState.value = currentState.copy(
                        isAnalyzing = false,
                        error = "⚠️ No model loaded. Please load a model from Settings first."
                    )
                    return@launch
                }

                Log.d("DamageVM", "Analyzing damage: ${currentState.damageDescription}")

                // Build analysis prompt
                val prompt = buildDamagePrompt(
                    description = currentState.damageDescription,
                    location = currentState.location,
                    structureType = currentState.structureType
                )

                // Configure generation options
                val options = RunAnywhereGenerationOptions(
                    maxTokens = 700,
                    temperature = 0.4f, // Lower for consistent analysis
                    topP = 0.9f,
                    streamingEnabled = false
                )

                // Generate assessment
                Log.d("DamageVM", "Sending prompt to model...")
                val response = RunAnywhere.generate(prompt, options)
                Log.d("DamageVM", "Received response: ${response.take(100)}...")

                // Parse assessment
                val assessment = parseAssessment(response, currentState)
                _assessments.value = listOf(assessment) + _assessments.value

                _uiState.value = currentState.copy(
                    isAnalyzing = false,
                    damageDescription = "" // Clear for next input
                )
                
                Log.d("DamageVM", "✅ Damage analysis complete")

            } catch (e: Exception) {
                Log.e("DamageVM", "❌ Damage analysis failed: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isAnalyzing = false,
                    error = "Failed to analyze damage: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    private fun buildDamagePrompt(
        description: String,
        location: String,
        structureType: StructureType
    ): String {
        val locationInfo = if (location.isNotBlank()) " at $location" else ""
        
        return """You are a structural engineer analyzing disaster damage. Assess this ${structureType.displayName}$locationInfo.

Damage Description: $description

Provide a detailed assessment covering:

1. SEVERITY LEVEL (Minor/Moderate/Severe/Critical/Catastrophic)
2. STRUCTURAL INTEGRITY (Stable/Compromised/Unsafe/Collapse Risk)
3. KEY FINDINGS (What damage was observed)
4. IMMEDIATE SAFETY CONCERNS (What dangers exist right now)
5. RECOMMENDED ACTIONS (What to do immediately)
6. ESTIMATED CASUALTIES (If any injuries/fatalities are likely based on damage)
7. PRIORITY (High/Medium/Low for response)

Be specific and professional. Focus on actionable safety information.

Assessment:"""
    }

    private fun parseAssessment(response: String, state: DamageUiState): DamageAssessment {
        // Extract key information from response
        var severity = extractSeverity(response)
        var structuralStatus = extractStructuralStatus(response)
        var findings = extractSection(response, "KEY FINDINGS", "IMMEDIATE")
        var safetyConcerns = extractSection(response, "SAFETY CONCERNS", "RECOMMENDED")
        var recommendations = extractSection(response, "RECOMMENDED ACTIONS", "ESTIMATED")
        var casualties = extractSection(response, "CASUALTIES", "PRIORITY")
        var priority = extractPriority(response)

        // Fallback to simple parsing if structured parsing fails
        if (severity == SeverityLevel.MODERATE && structuralStatus == StructuralStatus.COMPROMISED) {
            severity = determineSeverityFromText(state.damageDescription)
            structuralStatus = determineStatusFromText(state.damageDescription)
        }

        if (findings.isBlank()) {
            findings = "Damage observed to ${state.structureType.displayName}"
        }

        if (safetyConcerns.isBlank()) {
            safetyConcerns = generateSafetyConcerns(severity, structuralStatus)
        }

        if (recommendations.isBlank()) {
            recommendations = generateRecommendations(severity, structuralStatus)
        }

        return DamageAssessment(
            location = state.location.ifBlank { "Not specified" },
            structureType = state.structureType,
            description = state.damageDescription,
            severity = severity,
            structuralStatus = structuralStatus,
            findings = findings,
            safetyConcerns = safetyConcerns,
            recommendations = recommendations,
            estimatedCasualties = casualties.ifBlank { "Unable to determine from description" },
            priority = priority,
            timestamp = dateFormat.format(Date()),
            fullReport = response
        )
    }

    private fun extractSeverity(text: String): SeverityLevel {
        val lower = text.lowercase()
        return when {
            lower.contains("catastrophic") -> SeverityLevel.CATASTROPHIC
            lower.contains("critical") -> SeverityLevel.CRITICAL
            lower.contains("severe") -> SeverityLevel.SEVERE
            lower.contains("moderate") -> SeverityLevel.MODERATE
            lower.contains("minor") -> SeverityLevel.MINOR
            else -> SeverityLevel.MODERATE
        }
    }

    private fun extractStructuralStatus(text: String): StructuralStatus {
        val lower = text.lowercase()
        return when {
            lower.contains("collapse") -> StructuralStatus.COLLAPSE_RISK
            lower.contains("unsafe") -> StructuralStatus.UNSAFE
            lower.contains("compromised") -> StructuralStatus.COMPROMISED
            lower.contains("stable") -> StructuralStatus.STABLE
            else -> StructuralStatus.COMPROMISED
        }
    }

    private fun extractPriority(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("high priority") || lower.contains("urgent") -> "HIGH"
            lower.contains("low priority") -> "LOW"
            else -> "MEDIUM"
        }
    }

    private fun extractSection(text: String, startMarker: String, endMarker: String): String {
        val lines = text.lines()
        var inSection = false
        val sectionLines = mutableListOf<String>()

        for (line in lines) {
            val trimmed = line.trim()
            
            if (trimmed.contains(startMarker, ignoreCase = true)) {
                inSection = true
                // Add content after the marker if present
                val afterMarker = trimmed.substringAfter(":", "").trim()
                if (afterMarker.isNotBlank() && !afterMarker.contains(startMarker, ignoreCase = true)) {
                    sectionLines.add(afterMarker)
                }
                continue
            }
            
            if (inSection && trimmed.contains(endMarker, ignoreCase = true)) {
                break
            }
            
            if (inSection && trimmed.isNotBlank()) {
                // Skip lines that are just section headers
                if (!trimmed.matches(Regex("^\\d+\\..*"))) {
                    sectionLines.add(trimmed)
                }
            }
        }

        return sectionLines.joinToString(" ").take(200).trim()
    }

    private fun determineSeverityFromText(description: String): SeverityLevel {
        val lower = description.lowercase()
        return when {
            lower.contains("complete") || lower.contains("total") || lower.contains("destroyed") -> SeverityLevel.CATASTROPHIC
            lower.contains("major") || lower.contains("severe") || lower.contains("extensive") -> SeverityLevel.SEVERE
            lower.contains("significant") || lower.contains("substantial") -> SeverityLevel.SEVERE
            lower.contains("moderate") || lower.contains("partial") -> SeverityLevel.MODERATE
            lower.contains("minor") || lower.contains("small") || lower.contains("slight") -> SeverityLevel.MINOR
            else -> SeverityLevel.MODERATE
        }
    }

    private fun determineStatusFromText(description: String): StructuralStatus {
        val lower = description.lowercase()
        return when {
            lower.contains("collapse") || lower.contains("fallen") -> StructuralStatus.COLLAPSE_RISK
            lower.contains("unsafe") || lower.contains("dangerous") || lower.contains("critical") -> StructuralStatus.UNSAFE
            lower.contains("damage") || lower.contains("crack") || lower.contains("broken") -> StructuralStatus.COMPROMISED
            else -> StructuralStatus.STABLE
        }
    }

    private fun generateSafetyConcerns(severity: SeverityLevel, status: StructuralStatus): String {
        return when {
            status == StructuralStatus.COLLAPSE_RISK || severity == SeverityLevel.CATASTROPHIC -> 
                "Immediate evacuation required. Risk of complete structural failure. Stay away from building."
            status == StructuralStatus.UNSAFE || severity == SeverityLevel.CRITICAL ->
                "Do not enter structure. Secondary collapse risk. Establish safety perimeter."
            severity == SeverityLevel.SEVERE ->
                "Entry only by trained personnel with safety equipment. Monitor for deterioration."
            else ->
                "Exercise caution. Avoid damaged areas. Monitor for changes."
        }
    }

    private fun generateRecommendations(severity: SeverityLevel, status: StructuralStatus): String {
        return when {
            status == StructuralStatus.COLLAPSE_RISK || severity == SeverityLevel.CATASTROPHIC ->
                "1. Evacuate immediately 2. Establish 100m safety zone 3. Request structural engineer 4. Document for insurance"
            status == StructuralStatus.UNSAFE || severity == SeverityLevel.CRITICAL ->
                "1. No entry without clearance 2. Post warning signs 3. Arrange professional inspection 4. Secure perimeter"
            severity == SeverityLevel.SEVERE ->
                "1. Limit access to essential personnel 2. Wear protective equipment 3. Schedule detailed assessment 4. Plan repairs"
            else ->
                "1. Document damage with photos 2. Schedule inspection 3. Monitor for changes 4. Plan maintenance"
        }
    }

    fun clearAssessments() {
        _assessments.value = emptyList()
    }
}

// Data classes
enum class StructureType(val displayName: String, val icon: String) {
    BUILDING("Building", "🏢"),
    BRIDGE("Bridge", "🌉"),
    ROAD("Road", "🛣️"),
    INFRASTRUCTURE("Infrastructure", "⚡"),
    RESIDENTIAL("Residential", "🏠"),
    COMMERCIAL("Commercial", "🏪")
}

enum class SeverityLevel(val displayName: String, val color: Long) {
    MINOR("Minor", 0xFF4CAF50),
    MODERATE("Moderate", 0xFF2196F3),
    SEVERE("Severe", 0xFFFF9800),
    CRITICAL("Critical", 0xFFF44336),
    CATASTROPHIC("Catastrophic", 0xFF9C27B0)
}

enum class StructuralStatus(val displayName: String) {
    STABLE("Stable"),
    COMPROMISED("Compromised"),
    UNSAFE("Unsafe"),
    COLLAPSE_RISK("Collapse Risk")
}

data class DamageAssessment(
    val location: String,
    val structureType: StructureType,
    val description: String,
    val severity: SeverityLevel,
    val structuralStatus: StructuralStatus,
    val findings: String,
    val safetyConcerns: String,
    val recommendations: String,
    val estimatedCasualties: String,
    val priority: String,
    val timestamp: String,
    val fullReport: String
)
