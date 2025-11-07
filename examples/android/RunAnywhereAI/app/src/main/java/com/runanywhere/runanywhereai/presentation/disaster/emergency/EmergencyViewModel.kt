package com.runanywhere.runanywhereai.presentation.disaster.emergency

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.models.RunAnywhereGenerationOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmergencyUiState(
    val selectedCategory: EmergencyCategory = EmergencyCategory.FIRST_AID,
    val query: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null
)

class EmergencyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    private val _guides = MutableStateFlow<List<EmergencyGuide>>(emptyList())
    val guides: StateFlow<List<EmergencyGuide>> = _guides.asStateFlow()

    fun setCategory(category: EmergencyCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateQuery(value: String) {
        _uiState.value = _uiState.value.copy(query = value, error = null)
    }

    fun getGuidance() {
        val currentState = _uiState.value

        if (currentState.query.isBlank()) {
            _uiState.value = currentState.copy(error = "⚠️ Please enter your emergency question")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isGenerating = true, error = null)

                val currentModel = RunAnywhere.currentModel
                if (currentModel == null) {
                    _uiState.value = currentState.copy(
                        isGenerating = false,
                        error = "⚠️ No model loaded. Please load a model from Settings first."
                    )
                    return@launch
                }

                Log.d("EmergencyVM", "Getting guidance for: ${currentState.query}")

                val prompt = buildEmergencyPrompt(
                    category = currentState.selectedCategory,
                    query = currentState.query
                )

                val options = RunAnywhereGenerationOptions(
                    maxTokens = 700,
                    temperature = 0.3f, // Lower for accurate medical/safety info
                    topP = 0.9f,
                    streamingEnabled = false
                )

                Log.d("EmergencyVM", "Sending prompt to model...")
                val response = RunAnywhere.generate(prompt, options)
                Log.d("EmergencyVM", "Received response: ${response.take(100)}...")

                val guide = parseGuidance(response, currentState)
                _guides.value = listOf(guide) + _guides.value

                _uiState.value = currentState.copy(
                    isGenerating = false,
                    query = ""
                )

                Log.d("EmergencyVM", "✅ Guidance generated successfully")

            } catch (e: Exception) {
                Log.e("EmergencyVM", "❌ Guidance generation failed: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isGenerating = false,
                    error = "Failed to get guidance: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    private fun buildEmergencyPrompt(category: EmergencyCategory, query: String): String {
        val categoryContext = when (category) {
            EmergencyCategory.FIRST_AID -> """You are an emergency first aid instructor providing life-saving medical guidance.

CRITICAL: This is emergency medical information. Be clear, accurate, and actionable.

Emergency Situation: $query

Provide:
1. IMMEDIATE ACTIONS: What to do RIGHT NOW (step by step)
2. WARNING SIGNS: Critical symptoms to watch for
3. WHAT NOT TO DO: Common mistakes to avoid
4. WHEN TO SEEK HELP: When professional medical care is essential
5. SUPPLIES NEEDED: What materials/tools are required

Keep instructions simple and clear. Number all steps."""

            EmergencyCategory.SURVIVAL -> """You are a survival expert providing disaster survival guidance.

Survival Situation: $query

Provide practical survival guidance covering:
1. IMMEDIATE PRIORITIES: Most important actions first
2. SHELTER: How to find or create protection
3. WATER: How to find, purify, or ration water
4. FOOD: Emergency food sources if applicable
5. SIGNALING: How to signal for help
6. SAFETY: Dangers to avoid

Be practical and specific."""

            EmergencyCategory.CPR -> """You are a CPR/AED instructor providing life-saving resuscitation guidance.

Situation: $query

Provide clear CPR guidance:
1. ASSESS THE SCENE: Safety check
2. CHECK RESPONSIVENESS: How to assess victim
3. CALL FOR HELP: Emergency contacts
4. CPR STEPS: Compressions and breathing (exact technique)
5. CONTINUE UNTIL: When to stop
6. SPECIAL CONSIDERATIONS: Age-specific or situation-specific notes

Be extremely clear on technique and sequence."""

            EmergencyCategory.NATURAL_DISASTER -> """You are an emergency management specialist providing disaster safety guidance.

Disaster Scenario: $query

Provide safety guidance covering:
1. IMMEDIATE DANGER: What threats exist right now
2. PROTECTIVE ACTIONS: How to stay safe
3. EVACUATION: When and how to evacuate
4. SHELTER IN PLACE: If evacuation isn't possible
5. AFTER THE EMERGENCY: Post-event safety
6. EMERGENCY CONTACTS: Who to notify

Focus on life safety."""

            EmergencyCategory.POISONING -> """You are a poison control specialist providing toxicology emergency guidance.

Poisoning Case: $query

Provide poison emergency guidance:
1. STOP EXPOSURE: Prevent further contact
2. CALL POISON CONTROL: Emphasize calling 1-800-222-1222 (US) immediately
3. SYMPTOMS TO MONITOR: What to watch for
4. FIRST AID: Safe immediate actions
5. DO NOT: Dangerous misconceptions (don't induce vomiting unless told)
6. INFORMATION TO COLLECT: What poison control needs to know

Safety is paramount."""

            EmergencyCategory.BURNS -> """You are an emergency medicine specialist providing burn treatment guidance.

Burn Injury: $query

Provide burn treatment guidance:
1. STOP THE BURNING: Remove from heat source
2. ASSESS SEVERITY: Degree of burn (1st, 2nd, 3rd)
3. COOL THE BURN: Proper cooling technique
4. COVER THE BURN: Appropriate dressing
5. PAIN MANAGEMENT: Safe pain relief
6. SEEK MEDICAL CARE: When to go to ER

Never use ice, butter, or oils on burns."""
        }

        return categoryContext
    }

    private fun parseGuidance(response: String, state: EmergencyUiState): EmergencyGuide {
        // Extract key sections
        val immediateActions = extractSection(response, "IMMEDIATE", "WARNING")
            ?: extractSection(response, "PRIORITIES", "SHELTER")
            ?: extractFirstParagraph(response)

        val warnings = extractSection(response, "WARNING", "WHAT NOT")
            ?: extractSection(response, "DANGER", "PROTECTIVE")
            ?: "Monitor the situation closely and seek professional help if condition worsens"

        val doNots = extractSection(response, "WHAT NOT", "WHEN TO")
            ?: extractSection(response, "DO NOT", "INFORMATION")
            ?: "Avoid making the situation worse. When in doubt, call emergency services"

        val steps = extractSteps(response)

        return EmergencyGuide(
            category = state.selectedCategory,
            query = state.query,
            immediateActions = immediateActions,
            warnings = warnings,
            doNots = doNots,
            steps = steps,
            fullGuidance = response
        )
    }

    private fun extractSection(text: String, startMarker: String, endMarker: String): String? {
        val lines = text.lines()
        var inSection = false
        val sectionLines = mutableListOf<String>()

        for (line in lines) {
            val trimmed = line.trim()

            if (trimmed.contains(startMarker, ignoreCase = true)) {
                inSection = true
                val afterMarker = trimmed.substringAfter(":", "").trim()
                if (afterMarker.isNotBlank() && !afterMarker.contains(
                        startMarker,
                        ignoreCase = true
                    )
                ) {
                    sectionLines.add(afterMarker)
                }
                continue
            }

            if (endMarker.isNotBlank() && inSection && trimmed.contains(
                    endMarker,
                    ignoreCase = true
                )
            ) {
                break
            }

            if (inSection && trimmed.isNotBlank() && !trimmed.matches(Regex("^\\d+\\..*"))) {
                sectionLines.add(trimmed)
            }
        }

        val result = sectionLines.joinToString(" ").take(300).trim()
        return if (result.isNotBlank()) result else null
    }

    private fun extractFirstParagraph(text: String): String {
        val lines = text.lines().filter { it.isNotBlank() }
        return lines.take(3).joinToString(" ").take(300)
    }

    private fun extractSteps(text: String): List<String> {
        val steps = mutableListOf<String>()
        val lines = text.lines()

        for (line in lines) {
            val trimmed = line.trim()
            // Look for numbered steps
            val stepMatch =
                Regex("^(\\d+|Step \\d+)[.:]?\\s*(.+)", RegexOption.IGNORE_CASE).find(trimmed)
            if (stepMatch != null) {
                val stepText = stepMatch.groupValues[2].trim()
                if (stepText.length > 10 && stepText.length < 150) {
                    steps.add(stepText)
                }
            }
        }

        return steps.take(8)
    }

    fun clearGuides() {
        _guides.value = emptyList()
    }
}

// Data classes
enum class EmergencyCategory(
    val displayName: String,
    val icon: String,
    val description: String
) {
    FIRST_AID("First Aid", "🩹", "Cuts, injuries, bleeding"),
    CPR("CPR & AED", "❤️", "Cardiac arrest, not breathing"),
    BURNS("Burns", "🔥", "Thermal, chemical, electrical"),
    POISONING("Poisoning", "☠️", "Ingestion, exposure, bites"),
    NATURAL_DISASTER("Natural Disaster", "🌪️", "Earthquake, flood, storm"),
    SURVIVAL("Survival", "🎒", "Shelter, water, signaling")
}

data class EmergencyGuide(
    val category: EmergencyCategory,
    val query: String,
    val immediateActions: String,
    val warnings: String,
    val doNots: String,
    val steps: List<String>,
    val fullGuidance: String
)
