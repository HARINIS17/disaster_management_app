package com.runanywhere.runanywhereai.data.offline

/**
 * Offline translator for emergency situations when AI models are unavailable.
 * Uses pre-defined common emergency phrases in multiple languages.
 */
class OfflineTranslator {

    private val emergencyPhrases = mapOf(
        // English to Spanish
        "en_es" to mapOf(
            "help" to "ayuda",
            "emergency" to "emergencia",
            "danger" to "peligro",
            "water" to "agua",
            "food" to "comida",
            "medical help" to "ayuda médica",
            "hospital" to "hospital",
            "earthquake" to "terremoto",
            "fire" to "fuego",
            "flood" to "inundación",
            "i need help" to "necesito ayuda",
            "where is hospital" to "dónde está el hospital",
            "call ambulance" to "llame una ambulancia"
        ),
        // English to French
        "en_fr" to mapOf(
            "help" to "aide",
            "emergency" to "urgence",
            "danger" to "danger",
            "water" to "eau",
            "food" to "nourriture",
            "medical help" to "aide médicale",
            "hospital" to "hôpital",
            "earthquake" to "tremblement de terre",
            "fire" to "feu",
            "flood" to "inondation",
            "i need help" to "j'ai besoin d'aide",
            "where is hospital" to "où est l'hôpital",
            "call ambulance" to "appelez une ambulance"
        ),
        // English to Chinese
        "en_zh" to mapOf(
            "help" to "帮助",
            "emergency" to "紧急情况",
            "danger" to "危险",
            "water" to "水",
            "food" to "食物",
            "medical help" to "医疗帮助",
            "hospital" to "医院",
            "earthquake" to "地震",
            "fire" to "火灾",
            "flood" to "洪水",
            "i need help" to "我需要帮助",
            "where is hospital" to "医院在哪里",
            "call ambulance" to "叫救护车"
        ),
        // English to Arabic
        "en_ar" to mapOf(
            "help" to "مساعدة",
            "emergency" to "طوارئ",
            "danger" to "خطر",
            "water" to "ماء",
            "food" to "طعام",
            "medical help" to "مساعدة طبية",
            "hospital" to "مستشفى",
            "earthquake" to "زلزال",
            "fire" to "حريق",
            "flood" to "فيضان",
            "i need help" to "أحتاج مساعدة",
            "where is hospital" to "أين المستشفى",
            "call ambulance" to "اتصل بالإسعاف"
        ),
        // English to Hindi
        "en_hi" to mapOf(
            "help" to "मदद",
            "emergency" to "आपातकाल",
            "danger" to "खतरा",
            "water" to "पानी",
            "food" to "भोज���",
            "medical help" to "चिकित्सा सहायता",
            "hospital" to "अस्पताल",
            "earthquake" to "भूकंप",
            "fire" to "आग",
            "flood" to "बाढ़",
            "i need help" to "मुझे मदद चाहिए",
            "where is hospital" to "अस्पताल कहाँ है",
            "call ambulance" to "एम्बुलेंस बुलाओ"
        )
    )

    fun translate(text: String, fromLang: String, toLang: String): String? {
        val key = "${fromLang.lowercase()}_${toLang.lowercase()}"
        val phraseMap = emergencyPhrases[key] ?: return null

        // Try exact match first
        val lowerText = text.lowercase().trim()
        phraseMap[lowerText]?.let { return it }

        // Try partial matches for common phrases
        for ((phrase, translation) in phraseMap) {
            if (lowerText.contains(phrase)) {
                return translation
            }
        }

        return null
    }

    fun isSupported(fromLang: String, toLang: String): Boolean {
        val key = "${fromLang.lowercase()}_${toLang.lowercase()}"
        return emergencyPhrases.containsKey(key)
    }

    fun getSupportedLanguages(): List<Pair<String, String>> {
        return listOf(
            "en" to "es",
            "en" to "fr",
            "en" to "zh",
            "en" to "ar",
            "en" to "hi"
        )
    }
}