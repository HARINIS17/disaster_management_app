package com.runanywhere.runanywhereai.presentation.disaster.navigation

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

data class SafeRoutesUiState(
    val startLocation: String = "",
    val destination: String = "",
    val hazards: String = "",
    val transportMode: TransportMode = TransportMode.WALKING,
    val isCalculating: Boolean = false,
    val error: String? = null
)

class SafeRoutesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SafeRoutesUiState())
    val uiState: StateFlow<SafeRoutesUiState> = _uiState.asStateFlow()

    private val _routes = MutableStateFlow<List<SafeRoute>>(emptyList())
    val routes: StateFlow<List<SafeRoute>> = _routes.asStateFlow()

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun updateStartLocation(value: String) {
        _uiState.value = _uiState.value.copy(startLocation = value, error = null)
    }

    fun updateDestination(value: String) {
        _uiState.value = _uiState.value.copy(destination = value, error = null)
    }

    fun updateHazards(value: String) {
        _uiState.value = _uiState.value.copy(hazards = value, error = null)
    }

    fun setTransportMode(mode: TransportMode) {
        _uiState.value = _uiState.value.copy(transportMode = mode)
    }

    fun calculateRoute() {
        val currentState = _uiState.value

        // Validation
        if (currentState.startLocation.isBlank()) {
            _uiState.value = currentState.copy(error = "⚠️ Please enter starting location")
            return
        }

        if (currentState.destination.isBlank()) {
            _uiState.value = currentState.copy(error = "⚠️ Please enter destination")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isCalculating = true, error = null)

                // Check if model is loaded
                val currentModel = RunAnywhere.currentModel
                if (currentModel == null) {
                    _uiState.value = currentState.copy(
                        isCalculating = false,
                        error = "⚠️ No model loaded. Please load a model from Settings first."
                    )
                    return@launch
                }

                Log.d(
                    "SafeRoutesVM",
                    "Calculating route from ${currentState.startLocation} to ${currentState.destination}"
                )

                // Build route planning prompt
                val prompt = buildRoutePrompt(
                    start = currentState.startLocation,
                    destination = currentState.destination,
                    hazards = currentState.hazards,
                    transportMode = currentState.transportMode
                )

                // Configure generation options
                val options = RunAnywhereGenerationOptions(
                    maxTokens = 800,
                    temperature = 0.4f,
                    topP = 0.9f,
                    streamingEnabled = false
                )

                // Generate route recommendations
                Log.d("SafeRoutesVM", "Sending prompt to model...")
                val response = RunAnywhere.generate(prompt, options)
                Log.d("SafeRoutesVM", "Received response: ${response.take(100)}...")

                // Parse routes
                val route = parseRoute(response, currentState)
                _routes.value = listOf(route) + _routes.value

                _uiState.value = currentState.copy(
                    isCalculating = false,
                    startLocation = "", // Clear for next input
                    destination = ""
                )

                Log.d("SafeRoutesVM", "✅ Route calculated successfully")

            } catch (e: Exception) {
                Log.e("SafeRoutesVM", "❌ Route calculation failed: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isCalculating = false,
                    error = "Failed to calculate route: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    private fun buildRoutePrompt(
        start: String,
        destination: String,
        hazards: String,
        transportMode: TransportMode
    ): String {
        val hazardInfo = if (hazards.isNotBlank()) {
            "\n\nKnown Hazards: $hazards"
        } else {
            ""
        }

        return """You are an emergency route planner helping people navigate during disasters.

START: $start
DESTINATION: $destination
TRANSPORT: ${transportMode.displayName}$hazardInfo

Provide a safe route plan with:

1. PRIMARY ROUTE: Best/safest path
2. WAYPOINTS: Key landmarks or intersections to follow
3. ESTIMATED TIME: How long it will take
4. SAFETY WARNINGS: Specific hazards to avoid
5. ALTERNATIVE OPTION: Backup route if primary is blocked
6. EMERGENCY CONTACTS: Suggest checkpoints or safe zones

Consider:
- Avoid flooded areas, collapsed structures, fires
- Prefer main roads with emergency services
- Account for ${transportMode.displayName} limitations
- Provide clear turn-by-turn style directions

Route Plan:"""
    }

    private fun parseRoute(response: String, state: SafeRoutesUiState): SafeRoute {
        // Extract information from response
        val primaryRoute = extractSection(response, "PRIMARY ROUTE", "WAYPOINTS")
            ?: extractSection(response, "Route", "Estimated")
            ?: "Follow main roads from ${state.startLocation} to ${state.destination}"

        val waypoints = extractWaypoints(response)
        val estimatedTime = extractEstimatedTime(response)
        val safetyWarnings = extractSection(response, "SAFETY WARNINGS", "ALTERNATIVE")
            ?: extractSection(response, "Warnings", "Alternative")
            ?: generateSafetyWarnings(state.hazards, state.transportMode)

        val alternativeRoute = extractSection(response, "ALTERNATIVE", "EMERGENCY")
            ?: extractSection(response, "Alternative", "Emergency")
            ?: "If blocked, seek local guidance or contact emergency services"

        val emergencyInfo = extractSection(response, "EMERGENCY", "")
            ?: "Look for emergency responders, police stations, or hospitals along the route"

        val routeSafety = determineRouteSafety(response, state.hazards)

        return SafeRoute(
            startLocation = state.startLocation,
            destination = state.destination,
            transportMode = state.transportMode,
            primaryRoute = primaryRoute,
            waypoints = waypoints,
            estimatedTime = estimatedTime,
            safetyWarnings = safetyWarnings,
            alternativeRoute = alternativeRoute,
            emergencyInfo = emergencyInfo,
            routeSafety = routeSafety,
            timestamp = dateFormat.format(Date()),
            fullPlan = response
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

        val result = sectionLines.joinToString(" ").take(250).trim()
        return if (result.isNotBlank()) result else null
    }

    private fun extractWaypoints(text: String): List<String> {
        val waypoints = mutableListOf<String>()
        val lines = text.lines()

        for (line in lines) {
            val trimmed = line.trim()
            // Look for numbered or bulleted lists
            if (trimmed.matches(Regex("^[\\d•\\-*]+\\.?\\s+.+")) &&
                !trimmed.contains("PRIMARY", ignoreCase = true) &&
                !trimmed.contains("ALTERNATIVE", ignoreCase = true)
            ) {
                val waypoint = trimmed.replaceFirst(Regex("^[\\d•\\-*]+\\.?\\s+"), "")
                if (waypoint.length < 100) {
                    waypoints.add(waypoint)
                }
            }
        }

        return waypoints.take(6) // Limit to 6 waypoints
    }

    private fun extractEstimatedTime(text: String): String {
        // Look for time estimates
        val timePatterns = listOf(
            Regex("(\\d+)\\s*(?:hours?|hrs?)", RegexOption.IGNORE_CASE),
            Regex("(\\d+)\\s*(?:minutes?|mins?)", RegexOption.IGNORE_CASE),
            Regex("(\\d+)-(\\d+)\\s*(?:hours?|hrs?)", RegexOption.IGNORE_CASE)
        )

        for (pattern in timePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.value
            }
        }

        return "Time varies based on conditions"
    }

    private fun generateSafetyWarnings(hazards: String, mode: TransportMode): String {
        val warnings = mutableListOf<String>()

        if (hazards.isNotBlank()) {
            warnings.add("Reported hazards: $hazards")
        }

        warnings.add("Stay alert for debris, damaged infrastructure, and emergency vehicles")

        when (mode) {
            TransportMode.WALKING -> warnings.add("Watch for unstable ground and fallen power lines")
            TransportMode.VEHICLE -> warnings.add("Check for road closures and bridge damage")
            TransportMode.BICYCLE -> warnings.add("Be cautious of debris on roads")
        }

        return warnings.joinToString(". ")
    }

    private fun determineRouteSafety(text: String, hazards: String): RouteSafety {
        val lower = text.lowercase()

        return when {
            lower.contains("dangerous") || lower.contains("high risk") ||
                    lower.contains("not recommended") || hazards.contains(
                "severe",
                ignoreCase = true
            ) ->
                RouteSafety.DANGEROUS

            lower.contains("caution") || lower.contains("moderate risk") ||
                    hazards.isNotBlank() ->
                RouteSafety.CAUTION

            lower.contains("safe") || lower.contains("clear") ->
                RouteSafety.SAFE

            else -> RouteSafety.UNKNOWN
        }
    }

    fun clearRoutes() {
        _routes.value = emptyList()
    }
}

// Data classes
enum class TransportMode(val displayName: String, val icon: String) {
    WALKING("Walking", "🚶"),
    VEHICLE("Vehicle", "🚗"),
    BICYCLE("Bicycle", "🚲")
}

enum class RouteSafety(val displayName: String, val color: Long) {
    SAFE("Safe Route", 0xFF4CAF50),
    CAUTION("Use Caution", 0xFFFF9800),
    DANGEROUS("High Risk", 0xFFF44336),
    UNKNOWN("Assess Conditions", 0xFF2196F3)
}

data class SafeRoute(
    val startLocation: String,
    val destination: String,
    val transportMode: TransportMode,
    val primaryRoute: String,
    val waypoints: List<String>,
    val estimatedTime: String,
    val safetyWarnings: String,
    val alternativeRoute: String,
    val emergencyInfo: String,
    val routeSafety: RouteSafety,
    val timestamp: String,
    val fullPlan: String
)
