package com.runanywhere.runanywhereai.presentation.disaster.emergency

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

data class EmergencyAlert(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val message: String,
    val severity: AlertSeverity,
    val timestamp: Long,
    val userId: String = "user_${System.currentTimeMillis()}"
)

enum class AlertSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class DangerZone(
    val centerLat: Double,
    val centerLon: Double,
    val alertCount: Int,
    val severity: AlertSeverity,
    val radius: Double // in meters
)

data class EmergencyAlertState(
    val currentLocation: Pair<Double, Double>? = null,
    val myAlert: EmergencyAlert? = null,
    val nearbyAlerts: List<EmergencyAlert> = emptyList(),
    val dangerZones: List<DangerZone> = emptyList(),
    val alertRadius: Double = 5000.0, // 5km default
    val isLoading: Boolean = false,
    val error: String? = null
)

class EmergencyAlertViewModel : ViewModel() {

    private val _state = MutableStateFlow(EmergencyAlertState())
    val state: StateFlow<EmergencyAlertState> = _state.asStateFlow()

    // Simulated database of alerts (in production, this would be a real backend)
    private val alertsDatabase = mutableListOf<EmergencyAlert>()

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                currentLocation = Pair(latitude, longitude),
                error = null
            )
            // Fetch nearby alerts
            fetchNearbyAlerts()
        }
    }

    fun broadcastEmergency(message: String, severity: AlertSeverity) {
        viewModelScope.launch {
            val location = _state.value.currentLocation
            if (location == null) {
                _state.value = _state.value.copy(
                    error = "Location not available. Please enable GPS."
                )
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)

            try {
                // Create alert
                val alert = EmergencyAlert(
                    id = "alert_${System.currentTimeMillis()}",
                    latitude = location.first,
                    longitude = location.second,
                    message = message,
                    severity = severity,
                    timestamp = System.currentTimeMillis()
                )

                // Add to database
                alertsDatabase.add(alert)

                _state.value = _state.value.copy(
                    myAlert = alert,
                    isLoading = false,
                    error = null
                )

                // Refresh nearby alerts and danger zones
                fetchNearbyAlerts()

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to broadcast alert: ${e.message}"
                )
            }
        }
    }

    fun cancelMyAlert() {
        viewModelScope.launch {
            val myAlert = _state.value.myAlert
            if (myAlert != null) {
                alertsDatabase.removeAll { it.id == myAlert.id }
                _state.value = _state.value.copy(myAlert = null)
                fetchNearbyAlerts()
            }
        }
    }

    fun setAlertRadius(radiusKm: Double) {
        viewModelScope.launch {
            _state.value = _state.value.copy(alertRadius = radiusKm * 1000)
            fetchNearbyAlerts()
        }
    }

    private fun fetchNearbyAlerts() {
        viewModelScope.launch {
            val location = _state.value.currentLocation ?: return@launch
            val radius = _state.value.alertRadius

            // Filter alerts within radius
            val nearby = alertsDatabase.filter { alert ->
                val distance = calculateDistance(
                    location.first, location.second,
                    alert.latitude, alert.longitude
                )
                distance <= radius && alert.id != _state.value.myAlert?.id
            }.sortedBy { alert ->
                calculateDistance(
                    location.first, location.second,
                    alert.latitude, alert.longitude
                )
            }

            // Detect danger zones (clusters of 3+ alerts within 1km)
            val dangerZones = detectDangerZones(nearby)

            _state.value = _state.value.copy(
                nearbyAlerts = nearby,
                dangerZones = dangerZones
            )
        }
    }

    private fun detectDangerZones(alerts: List<EmergencyAlert>): List<DangerZone> {
        val zones = mutableListOf<DangerZone>()
        val processed = mutableSetOf<String>()

        for (alert in alerts) {
            if (alert.id in processed) continue

            // Find all alerts within 1km of this alert
            val cluster = alerts.filter { other ->
                other.id != alert.id && calculateDistance(
                    alert.latitude, alert.longitude,
                    other.latitude, other.longitude
                ) <= 1000.0
            }

            if (cluster.size >= 2) { // 3+ alerts including this one
                // Mark all as processed
                processed.add(alert.id)
                cluster.forEach { processed.add(it.id) }

                // Calculate center
                val allInCluster = listOf(alert) + cluster
                val centerLat = allInCluster.map { it.latitude }.average()
                val centerLon = allInCluster.map { it.longitude }.average()

                // Determine severity
                val severity = when {
                    allInCluster.size >= 10 -> AlertSeverity.CRITICAL
                    allInCluster.size >= 5 -> AlertSeverity.HIGH
                    else -> AlertSeverity.MEDIUM
                }

                zones.add(
                    DangerZone(
                        centerLat = centerLat,
                        centerLon = centerLon,
                        alertCount = allInCluster.size,
                        severity = severity,
                        radius = 1000.0
                    )
                )
            }
        }

        return zones
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // meters

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> "${meters.toInt()}m"
            else -> "${"%.1f".format(meters / 1000)}km"
        }
    }

    // Simulate some initial alerts for demonstration
    fun addDemoAlerts(userLat: Double, userLon: Double) {
        viewModelScope.launch {
            // Add some demo alerts around user's location
            val demoAlerts = listOf(
                EmergencyAlert(
                    id = "demo1",
                    latitude = userLat + 0.01,
                    longitude = userLon + 0.01,
                    message = "Trapped in building",
                    severity = AlertSeverity.CRITICAL,
                    timestamp = System.currentTimeMillis() - 300000 // 5 min ago
                ),
                EmergencyAlert(
                    id = "demo2",
                    latitude = userLat - 0.02,
                    longitude = userLon - 0.01,
                    message = "Medical assistance needed",
                    severity = AlertSeverity.HIGH,
                    timestamp = System.currentTimeMillis() - 600000 // 10 min ago
                ),
                EmergencyAlert(
                    id = "demo3",
                    latitude = userLat + 0.015,
                    longitude = userLon + 0.012,
                    message = "Building collapse",
                    severity = AlertSeverity.CRITICAL,
                    timestamp = System.currentTimeMillis() - 120000 // 2 min ago
                )
            )

            alertsDatabase.addAll(demoAlerts)
            fetchNearbyAlerts()
        }
    }
}