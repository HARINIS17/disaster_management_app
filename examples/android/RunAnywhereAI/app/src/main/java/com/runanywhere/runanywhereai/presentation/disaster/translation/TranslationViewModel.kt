package com.runanywhere.runanywhereai.presentation.disaster.translation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.runanywhereai.data.offline.OfflineTranslator
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.models.RunAnywhereGenerationOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class TranslationUiState(
    val sourceLanguage: Language = Language.commonLanguages[0], // English
    val targetLanguage: Language = Language.commonLanguages.find { it.code == "es" } 
        ?: Language.commonLanguages[1], // Spanish
    val inputText: String = "",
    val isTranslating: Boolean = false,
    val error: String? = null,
    val usingAI: Boolean = false // Indicates if AI or fallback is used
)

class TranslationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()

    private val _translations = MutableStateFlow<List<Translation>>(emptyList())
    val translations: StateFlow<List<Translation>> = _translations.asStateFlow()

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    // Emergency translation dictionary (common disaster phrases)
    private val emergencyPhrases = mapOf(
        "en_es" to mapOf(
            "help" to "ayuda",
            "emergency" to "emergencia",
            "danger" to "peligro",
            "safe" to "seguro",
            "water" to "agua",
            "food" to "comida",
            "medical" to "médico",
            "evacuation" to "evacuación",
            "shelter" to "refugio",
            "need help" to "necesito ayuda",
            "i am safe" to "estoy seguro",
            "where is" to "dónde está",
            "hospital" to "hospital",
            "police" to "policía",
            "fire" to "fuego",
            "earthquake" to "terremoto",
            "flood" to "inundación"
        ),
        "en_fr" to mapOf(
            "help" to "aide",
            "emergency" to "urgence",
            "danger" to "danger",
            "safe" to "sûr",
            "water" to "eau",
            "food" to "nourriture",
            "medical" to "médical",
            "evacuation" to "évacuation",
            "shelter" to "abri"
        ),
        "en_hi" to mapOf(
            "help" to "मदद",
            "emergency" to "आपातकाल",
            "danger" to "खतरा",
            "safe" to "सुरक्षित",
            "water" to "पानी",
            "food" to "भोजन"
        )
        // Add more language pairs as needed
    )

    fun setSourceLanguage(language: Language) {
        _uiState.value = _uiState.value.copy(sourceLanguage = language)
    }

    fun setTargetLanguage(language: Language) {
        _uiState.value = _uiState.value.copy(targetLanguage = language)
    }

    fun swapLanguages() {
        _uiState.value = _uiState.value.copy(
            sourceLanguage = _uiState.value.targetLanguage,
            targetLanguage = _uiState.value.sourceLanguage
        )
    }

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text, error = null)
    }

    fun translate() {
        val currentState = _uiState.value
        if (currentState.inputText.isBlank()) return

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isTranslating = true, error = null)

                // Check if model is loaded for AI translation
                val currentModel = RunAnywhere.currentModel

                val translatedText = if (currentModel != null) {
                    // Use AI translation
                    Log.d("Translation", "Using AI model for translation")
                    _uiState.value = currentState.copy(usingAI = true)
                    translateWithAI(currentState)
                } else {
                    // Use fallback dictionary translation
                    Log.d("Translation", "Model not available - using emergency dictionary")
                    _uiState.value = currentState.copy(usingAI = false)
                    translateWithDictionary(currentState)
                }

                // Add to translation history
                val newTranslation = Translation(
                    originalText = currentState.inputText,
                    translatedText = translatedText,
                    sourceLanguage = currentState.sourceLanguage,
                    targetLanguage = currentState.targetLanguage,
                    timestamp = dateFormat.format(Date())
                )

                _translations.value = _translations.value + newTranslation

                // Clear input
                _uiState.value = currentState.copy(
                    inputText = "",
                    isTranslating = false
                )

                Log.d("Translation", "✅ Translation complete: $translatedText")

            } catch (e: Exception) {
                Log.e("Translation", "❌ Translation failed: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isTranslating = false,
                    error = "Translation failed: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    /**
     * AI-powered translation (when model is available)
     */
    private suspend fun translateWithAI(state: TranslationUiState): String {
        val prompt = buildTranslationPrompt(
            text = state.inputText,
            fromLang = state.sourceLanguage.displayName,
            toLang = state.targetLanguage.displayName
        )

        val options = RunAnywhereGenerationOptions(
            maxTokens = 500,
            temperature = 0.3f,
            topP = 0.9f,
            streamingEnabled = false
        )

        val translatedText = RunAnywhere.generate(prompt, options)
        return cleanTranslationResponse(translatedText)
    }

    /**
     * Dictionary-based translation (fallback when no model)
     * Works offline, instant, no AI needed
     */
    private fun translateWithDictionary(state: TranslationUiState): String {
        val langPair = "${state.sourceLanguage.code}_${state.targetLanguage.code}"
        val dictionary = emergencyPhrases[langPair]

        if (dictionary != null) {
            // Try exact match first
            val lowerInput = state.inputText.lowercase().trim()
            val exactMatch = dictionary[lowerInput]
            if (exactMatch != null) {
                return exactMatch
            }

            // Try partial match (contains key phrase)
            for ((key, value) in dictionary) {
                if (lowerInput.contains(key)) {
                    return value + " (partial match)"
                }
            }
        }

        // If no dictionary available or no match found
        return "[Translation: ${state.targetLanguage.displayName}] ${state.inputText}\n\n" +
                "ℹ️ Dictionary translation - load AI model for better quality"
    }

    private fun buildTranslationPrompt(text: String, fromLang: String, toLang: String): String {
        return """Translate this text from $fromLang to $toLang. Only provide the translation, nothing else.

Text: "$text"

Translation:"""
    }

    private fun cleanTranslationResponse(response: String): String {
        // Remove common prefixes and cleanup
        var cleaned = response.trim()

        // Remove quotes if wrapped
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length - 1)
        }

        // Remove common prefixes (case insensitive)
        val prefixes = listOf(
            "Translation:",
            "Translation in",
            "Here is the translation:",
            "The translation is:",
            "Translated text:",
            "Answer:",
            "Result:"
        )

        for (prefix in prefixes) {
            val index = cleaned.indexOf(prefix, ignoreCase = true)
            if (index != -1) {
                cleaned = cleaned.substring(index + prefix.length).trim()
            }
        }

        // Remove remaining quotes
        cleaned = cleaned.replace("\"", "").replace("'", "")

        // Take only the first sentence/paragraph if model generated extra text
        val lines = cleaned.lines().filter { it.isNotBlank() }
        if (lines.isNotEmpty()) {
            // Return first non-empty line as the translation
            cleaned = lines[0].trim()
        }

        return cleaned.trim()
    }

    fun startVoiceInput() {
        // TODO: Integrate with Whisper for voice transcription
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                error = "🎙️ Voice input coming soon! Will integrate with WhisperKit for speech-to-text."
            )
        }
    }

    fun clearTranslations() {
        _translations.value = emptyList()
    }
}
