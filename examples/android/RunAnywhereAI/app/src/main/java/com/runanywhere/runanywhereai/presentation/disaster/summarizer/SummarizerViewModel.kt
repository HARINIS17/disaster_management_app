package com.runanywhere.runanywhereai.presentation.disaster.summarizer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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

data class SummarizerUiState(
    val reportType: ReportType = ReportType.SITREP,
    val fieldNotes: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null
)

class SummarizerViewModel(
    private val context: Context? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(SummarizerUiState())
    val uiState: StateFlow<SummarizerUiState> = _uiState.asStateFlow()

    private val _reports = MutableStateFlow<List<DisasterReport>>(emptyList())
    val reports: StateFlow<List<DisasterReport>> = _reports.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun setReportType(type: ReportType) {
        _uiState.value = _uiState.value.copy(reportType = type)
    }

    fun updateFieldNotes(notes: String) {
        _uiState.value = _uiState.value.copy(fieldNotes = notes, error = null)
    }

    fun generateReport() {
        val currentState = _uiState.value
        if (currentState.fieldNotes.isBlank()) return

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isGenerating = true, error = null)

                // Check if model is loaded
                val currentModel = RunAnywhere.currentModel
                if (currentModel == null) {
                    _uiState.value = currentState.copy(
                        isGenerating = false,
                        error = "⚠️ No model loaded. Please load a model from Settings first."
                    )
                    return@launch
                }

                Log.d("Summarizer", "Generating ${currentState.reportType.displayName}")
                Log.d("Summarizer", "Field notes: ${currentState.fieldNotes}")

                // Build prompt based on report type
                val prompt = buildReportPrompt(
                    fieldNotes = currentState.fieldNotes,
                    reportType = currentState.reportType
                )

                // Configure generation options for report generation
                val options = RunAnywhereGenerationOptions(
                    maxTokens = 800, // Reports can be longer
                    temperature = 0.5f, // Balanced between creativity and accuracy
                    topP = 0.9f,
                    streamingEnabled = false // Use non-streaming for complete reports
                )

                // Generate report using RunAnywhere SDK
                Log.d("Summarizer", "Sending prompt to model...")
                val reportContent = RunAnywhere.generate(prompt, options)
                Log.d("Summarizer", "Received response: ${reportContent.take(100)}...")

                // Clean up the response
                val cleanedContent = cleanReportResponse(reportContent)

                // Create report object
                val report = DisasterReport(
                    type = currentState.reportType,
                    content = cleanedContent,
                    timestamp = dateFormat.format(Date())
                )

                _reports.value = listOf(report) + _reports.value

                // Clear field notes
                _uiState.value = currentState.copy(
                    fieldNotes = "",
                    isGenerating = false
                )

                Log.d("Summarizer", "✅ Report generated successfully")

            } catch (e: Exception) {
                Log.e("Summarizer", "❌ Report generation failed: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isGenerating = false,
                    error = "Failed to generate report: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    private fun buildReportPrompt(fieldNotes: String, reportType: ReportType): String {
        // Simplified prompts optimized for small language models
        return when (reportType) {
            ReportType.SITREP -> """Create a situation report from these field notes. Include: current situation, casualties, damages, resources, and next steps.

Field Notes: $fieldNotes

Situation Report:"""

            ReportType.CASUALTY -> """Create a casualty report from these field notes. Include: total casualties, injury severity levels, medical needs, and evacuation status.

Field Notes: $fieldNotes

Casualty Report:"""

            ReportType.DAMAGE -> """Create a damage assessment from these field notes. Include: structural damage, infrastructure status, severity levels, and affected population.

Field Notes: $fieldNotes

Damage Assessment:"""

            ReportType.RESOURCE -> """Create a resource request from these field notes. List: urgent needs, quantities required, priorities, and delivery location.

Field Notes: $fieldNotes

Resource Request:"""

            ReportType.INCIDENT -> """Create an incident report from these field notes. Include: incident type, time/location, description, response actions, and current status.

Field Notes: $fieldNotes

Incident Report:"""
        }
    }

    private fun cleanReportResponse(response: String): String {
        var cleaned = response.trim()

        // Remove common prefixes
        val prefixes = listOf(
            "Situation Report:",
            "Casualty Report:",
            "Damage Assessment:",
            "Resource Request:",
            "Incident Report:",
            "Report:",
            "Here is the",
            "Here's the"
        )

        for (prefix in prefixes) {
            val index = cleaned.indexOf(prefix, ignoreCase = true)
            if (index != -1) {
                cleaned = cleaned.substring(index + prefix.length).trim()
            }
        }

        // Remove quotes
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length - 1)
        }

        return cleaned.trim()
    }

    fun copyReport(report: DisasterReport) {
        try {
            context?.let { ctx ->
                val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    "Disaster Report - ${report.type.displayName}",
                    """
                    ${report.type.icon} ${report.type.displayName}
                    Generated: ${report.timestamp}
                    
                    ${report.content}
                    """.trimIndent()
                )
                clipboard.setPrimaryClip(clip)
                Log.d("Summarizer", "✅ Report copied to clipboard")
            } ?: run {
                Log.w("Summarizer", "⚠️ Context not available for clipboard copy")
            }
        } catch (e: Exception) {
            Log.e("Summarizer", "❌ Failed to copy report: ${e.message}", e)
        }
    }

    fun clearReports() {
        _reports.value = emptyList()
    }
}

// ViewModel Factory to provide context
class SummarizerViewModelFactory(
    private val context: Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SummarizerViewModel::class.java)) {
            return SummarizerViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
