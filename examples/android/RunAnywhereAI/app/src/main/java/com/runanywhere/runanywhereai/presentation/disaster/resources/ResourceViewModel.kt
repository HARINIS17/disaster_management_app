package com.runanywhere.runanywhereai.presentation.disaster.resources

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.models.RunAnywhereGenerationOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResourceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ResourceUiState())
    val uiState: StateFlow<ResourceUiState> = _uiState.asStateFlow()

    private val _predictions = MutableStateFlow<List<ResourcePrediction>>(emptyList())
    val predictions: StateFlow<List<ResourcePrediction>> = _predictions.asStateFlow()

    fun updateSituation(value: String) {
        _uiState.value = _uiState.value.copy(situationDesc = value, error = null)
    }

    fun updateDuration(value: String) {
        _uiState.value = _uiState.value.copy(duration = value, error = null)
    }

    fun updateLocation(value: String) {
        _uiState.value = _uiState.value.copy(location = value, error = null)
    }

    fun predictResources() {
        val currentState = _uiState.value
        
        // Validation
        if (currentState.situationDesc.isBlank()) {
            _uiState.value = currentState.copy(error = "⚠️ Please enter the number of affected people")
            return
        }
        
        if (currentState.duration.isBlank()) {
            _uiState.value = currentState.copy(error = "⚠️ Please enter the estimated duration")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isPredicting = true, error = null)

                // Check if model is loaded
                val currentModel = RunAnywhere.currentModel
                if (currentModel == null) {
                    _uiState.value = currentState.copy(
                        isPredicting = false,
                        error = "⚠️ No model loaded. Please load a model from Settings first."
                    )
                    return@launch
                }

                Log.d("ResourceVM", "Predicting resources for ${currentState.situationDesc} people, ${currentState.duration} days")

                // Build simplified prompt
                val prompt = buildResourcePrompt(
                    people = currentState.situationDesc,
                    duration = currentState.duration,
                    location = currentState.location
                )

                // Configure generation options
                val options = RunAnywhereGenerationOptions(
                    maxTokens = 600,
                    temperature = 0.4f, // Lower for more consistent output
                    topP = 0.9f,
                    streamingEnabled = false
                )

                // Generate predictions
                Log.d("ResourceVM", "Sending prompt to model...")
                val response = RunAnywhere.generate(prompt, options)
                Log.d("ResourceVM", "Received response: ${response.take(100)}...")

                // Parse predictions
                val predictions = parseResourcePredictions(response, currentState.situationDesc, currentState.duration)
                _predictions.value = predictions

                _uiState.value = currentState.copy(isPredicting = false)
                Log.d("ResourceVM", "✅ Generated ${predictions.size} resource predictions")

            } catch (e: Exception) {
                Log.e("ResourceVM", "❌ Resource prediction failed: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isPredicting = false,
                    error = "Failed to predict resources: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    private fun buildResourcePrompt(people: String, duration: String, location: String): String {
        // Simplified prompt optimized for small models
        val locationInfo = if (location.isNotBlank()) " in $location" else ""
        
        return """You are a disaster logistics expert. Calculate resource needs for $people people$locationInfo for $duration days.

List the critical resources needed. For each resource, specify:
- Name
- Estimated quantity
- Why it's needed

Critical resources to include:
1. Water (liters needed)
2. Food (meals needed)
3. Medical supplies (first aid kits)
4. Shelter (tents/emergency housing)
5. Blankets/bedding
6. Hygiene kits
7. Clothing
8. Power generators

Calculate based on standard disaster response guidelines:
- Water: 2-3 liters per person per day
- Food: 3 meals per person per day
- Medical: 1 kit per 50 people
- Shelter: 1 tent per 5 people

Resource List:"""
    }

    private fun parseResourcePredictions(
        response: String, 
        people: String, 
        duration: String
    ): List<ResourcePrediction> {
        val predictions = mutableListOf<ResourcePrediction>()

        try {
            // Try to extract structured information from the response
            val lines = response.lines().filter { it.isNotBlank() }
            
            var currentCategory = ""
            var currentQuantity = ""
            var currentRationale = ""
            
            for (line in lines) {
                val trimmed = line.trim()
                
                // Look for resource categories
                when {
                    trimmed.contains("water", ignoreCase = true) -> {
                        if (currentCategory.isNotEmpty()) {
                            addPrediction(predictions, currentCategory, currentQuantity, currentRationale)
                        }
                        currentCategory = "Water"
                        currentQuantity = extractQuantity(trimmed, "liters") ?: calculateWater(people, duration)
                        currentRationale = extractRationale(trimmed) ?: "2-3L per person per day"
                    }
                    trimmed.contains("food", ignoreCase = true) || trimmed.contains("meal", ignoreCase = true) -> {
                        if (currentCategory.isNotEmpty()) {
                            addPrediction(predictions, currentCategory, currentQuantity, currentRationale)
                        }
                        currentCategory = "Food"
                        currentQuantity = extractQuantity(trimmed, "meals") ?: calculateFood(people, duration)
                        currentRationale = extractRationale(trimmed) ?: "3 meals per person per day"
                    }
                    trimmed.contains("medical", ignoreCase = true) -> {
                        if (currentCategory.isNotEmpty()) {
                            addPrediction(predictions, currentCategory, currentQuantity, currentRationale)
                        }
                        currentCategory = "Medical Supplies"
                        currentQuantity = extractQuantity(trimmed, "kits") ?: calculateMedical(people)
                        currentRationale = extractRationale(trimmed) ?: "First aid and emergency medical care"
                    }
                    trimmed.contains("shelter", ignoreCase = true) || trimmed.contains("tent", ignoreCase = true) -> {
                        if (currentCategory.isNotEmpty()) {
                            addPrediction(predictions, currentCategory, currentQuantity, currentRationale)
                        }
                        currentCategory = "Shelter"
                        currentQuantity = extractQuantity(trimmed, "tents") ?: calculateShelter(people)
                        currentRationale = extractRationale(trimmed) ?: "Emergency housing for displaced families"
                    }
                    trimmed.contains("blanket", ignoreCase = true) -> {
                        if (currentCategory.isNotEmpty()) {
                            addPrediction(predictions, currentCategory, currentQuantity, currentRationale)
                        }
                        currentCategory = "Blankets"
                        currentQuantity = extractQuantity(trimmed, "units") ?: calculateBlankets(people)
                        currentRationale = extractRationale(trimmed) ?: "Warmth and comfort"
                    }
                    trimmed.contains("hygiene", ignoreCase = true) -> {
                        if (currentCategory.isNotEmpty()) {
                            addPrediction(predictions, currentCategory, currentQuantity, currentRationale)
                        }
                        currentCategory = "Hygiene Kits"
                        currentQuantity = extractQuantity(trimmed, "kits") ?: calculateHygiene(people)
                        currentRationale = extractRationale(trimmed) ?: "Sanitation and disease prevention"
                    }
                }
            }
            
            // Add the last resource
            if (currentCategory.isNotEmpty()) {
                addPrediction(predictions, currentCategory, currentQuantity, currentRationale)
            }

            // If AI didn't provide good output, use calculated predictions
            if (predictions.size < 4) {
                Log.w("ResourceVM", "AI output insufficient, using calculated predictions")
                return generateCalculatedPredictions(people, duration)
            }

        } catch (e: Exception) {
            Log.e("ResourceVM", "Error parsing predictions: ${e.message}", e)
            return generateCalculatedPredictions(people, duration)
        }

        return predictions
    }

    private fun addPrediction(
        list: MutableList<ResourcePrediction>,
        category: String,
        quantity: String,
        rationale: String
    ) {
        val (icon, priority) = getResourceDetails(category)
        list.add(
            ResourcePrediction(
                category = category,
                quantity = quantity,
                priority = priority,
                rationale = rationale,
                icon = icon
            )
        )
    }

    private fun getResourceDetails(category: String): Pair<String, String> {
        return when (category.lowercase()) {
            "water" -> "💧" to "CRITICAL"
            "food" -> "🍲" to "CRITICAL"
            "medical supplies" -> "💊" to "CRITICAL"
            "shelter" -> "⛺" to "HIGH"
            "blankets" -> "🛏️" to "HIGH"
            "hygiene kits" -> "🧼" to "HIGH"
            "clothing" -> "👕" to "MEDIUM"
            "power" -> "🔌" to "MEDIUM"
            else -> "📦" to "MEDIUM"
        }
    }

    private fun extractQuantity(line: String, unit: String): String? {
        // Try to extract numbers from the line
        val numbers = Regex("\\d+[,\\d]*").findAll(line).map { it.value.replace(",", "") }
        return numbers.firstOrNull()?.let { "$it $unit" }
    }

    private fun extractRationale(line: String): String? {
        // Extract text after common separators
        return line.split(":", "-", "–").getOrNull(1)?.trim()?.take(100)
    }

    // Calculation helpers
    private fun calculateWater(people: String, duration: String): String {
        val numPeople = people.toIntOrNull() ?: 100
        val numDays = duration.toIntOrNull() ?: 7
        val liters = numPeople * 2.5 * numDays // 2.5L per person per day
        return "${String.format("%,d", liters.toInt())} liters"
    }

    private fun calculateFood(people: String, duration: String): String {
        val numPeople = people.toIntOrNull() ?: 100
        val numDays = duration.toIntOrNull() ?: 7
        val meals = numPeople * 3 * numDays // 3 meals per day
        return "${String.format("%,d", meals)} meals"
    }

    private fun calculateMedical(people: String): String {
        val numPeople = people.toIntOrNull() ?: 100
        val kits = (numPeople / 50).coerceAtLeast(10) // 1 kit per 50 people
        return "$kits first aid kits"
    }

    private fun calculateShelter(people: String): String {
        val numPeople = people.toIntOrNull() ?: 100
        val tents = (numPeople / 5).coerceAtLeast(10) // 1 tent per 5 people
        return "$tents emergency tents"
    }

    private fun calculateBlankets(people: String): String {
        val numPeople = people.toIntOrNull() ?: 100
        val blankets = (numPeople * 1.5).toInt() // 1.5 per person
        return "${String.format("%,d", blankets)} blankets"
    }

    private fun calculateHygiene(people: String): String {
        val numPeople = people.toIntOrNull() ?: 100
        val kits = (numPeople / 5).coerceAtLeast(20) // 1 kit per 5 people
        return "$kits hygiene kits"
    }

    private fun generateCalculatedPredictions(people: String, duration: String): List<ResourcePrediction> {
        return listOf(
            ResourcePrediction(
                category = "Water",
                quantity = calculateWater(people, duration),
                priority = "CRITICAL",
                rationale = "2.5 liters per person per day for drinking, cooking, and basic hygiene",
                icon = "💧"
            ),
            ResourcePrediction(
                category = "Food",
                quantity = calculateFood(people, duration),
                priority = "CRITICAL",
                rationale = "3 meals per person per day to meet nutritional requirements",
                icon = "🍲"
            ),
            ResourcePrediction(
                category = "Medical Supplies",
                quantity = calculateMedical(people),
                priority = "CRITICAL",
                rationale = "First aid kits for injuries, chronic conditions, and emergency medical care",
                icon = "💊"
            ),
            ResourcePrediction(
                category = "Shelter",
                quantity = calculateShelter(people),
                priority = "HIGH",
                rationale = "Emergency tents providing protection from weather and privacy for families",
                icon = "⛺"
            ),
            ResourcePrediction(
                category = "Blankets",
                quantity = calculateBlankets(people),
                priority = "HIGH",
                rationale = "Warmth and comfort, especially critical in cold weather conditions",
                icon = "🛏️"
            ),
            ResourcePrediction(
                category = "Hygiene Kits",
                quantity = calculateHygiene(people),
                priority = "HIGH",
                rationale = "Soap, sanitizer, and sanitation supplies to prevent disease spread",
                icon = "🧼"
            ),
            ResourcePrediction(
                category = "Clothing",
                quantity = "${(people.toIntOrNull() ?: 100) / 2} sets",
                priority = "MEDIUM",
                rationale = "Clean clothes for those who lost belongings",
                icon = "👕"
            ),
            ResourcePrediction(
                category = "Power Generators",
                quantity = "${((people.toIntOrNull() ?: 100) / 100).coerceAtLeast(2)} units",
                priority = "MEDIUM",
                rationale = "Emergency power for lighting, communications, and medical equipment",
                icon = "🔌"
            )
        )
    }
}
