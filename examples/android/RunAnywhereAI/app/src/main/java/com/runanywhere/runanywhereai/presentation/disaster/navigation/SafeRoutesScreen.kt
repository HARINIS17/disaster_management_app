package com.runanywhere.runanywhereai.presentation.disaster.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 🗺 Safe Routes Navigator
 * AI-powered route planning for disaster zones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeRoutesScreen(
    viewModel: SafeRoutesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val routes by viewModel.routes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🗺 Safe Routes")
                        Text(
                            "Emergency Navigation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (routes.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearRoutes() }) {
                            Icon(Icons.Default.Delete, "Clear routes")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Plan Your Route",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Transport Mode Selector
                    Text(
                        "Transport Mode",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TransportMode.values().forEach { mode ->
                            FilterChip(
                                selected = uiState.transportMode == mode,
                                onClick = { viewModel.setTransportMode(mode) },
                                label = { Text("${mode.icon} ${mode.displayName}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Start Location
                    OutlinedTextField(
                        value = uiState.startLocation,
                        onValueChange = { viewModel.updateStartLocation(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Start Location *") },
                        placeholder = { Text("e.g., Main St & 5th Ave") },
                        leadingIcon = { Icon(Icons.Default.MyLocation, null) },
                        enabled = !uiState.isCalculating
                    )

                    // Destination
                    OutlinedTextField(
                        value = uiState.destination,
                        onValueChange = { viewModel.updateDestination(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Destination *") },
                        placeholder = { Text("e.g., Community Center, Safe Zone") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        enabled = !uiState.isCalculating
                    )

                    // Known Hazards (optional)
                    OutlinedTextField(
                        value = uiState.hazards,
                        onValueChange = { viewModel.updateHazards(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Known Hazards (optional)") },
                        placeholder = { Text("e.g., flooded streets, building collapse") },
                        leadingIcon = { Icon(Icons.Default.Warning, null) },
                        enabled = !uiState.isCalculating,
                        maxLines = 2
                    )

                    // Error message
                    val errorMessage = uiState.error
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Calculate Button
                    Button(
                        onClick = { viewModel.calculateRoute() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isCalculating &&
                                uiState.startLocation.isNotBlank() &&
                                uiState.destination.isNotBlank()
                    ) {
                        if (uiState.isCalculating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Directions, null, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(if (uiState.isCalculating) "Calculating..." else "Find Safe Route")
                    }
                }
            }

            // Routes List
            if (routes.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(routes) { route ->
                        RouteCard(route)
                    }
                }
            }
        }
    }
}

@Composable
fun RouteCard(route: SafeRoute) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(route.routeSafety.color).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        route.transportMode.icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Column {
                        Text(
                            "${route.startLocation} → ${route.destination}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Generated at ${route.timestamp}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Safety and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SafetyIndicator(route.routeSafety, Modifier.weight(1f))
                TimeEstimate(route.estimatedTime, Modifier.weight(1f))
            }

            HorizontalDivider()

            // Primary Route
            RouteSection(
                title = "📍 Primary Route",
                content = route.primaryRoute,
                icon = Icons.Default.Route
            )

            // Waypoints
            if (route.waypoints.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.TurnRight,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Waypoints",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        route.waypoints.forEachIndexed { index, waypoint ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        "${index + 1}",
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    waypoint,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Safety Warnings (Highlighted)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                RouteSection(
                    title = "⚠️ Safety Warnings",
                    content = route.safetyWarnings,
                    icon = Icons.Default.Warning
                )
            }

            // Alternative Route
            RouteSection(
                title = "🔄 Alternative Route",
                content = route.alternativeRoute,
                icon = Icons.Default.AltRoute
            )

            // Emergency Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                )
            ) {
                RouteSection(
                    title = "🆘 Emergency Information",
                    content = route.emergencyInfo,
                    icon = Icons.Default.LocalHospital
                )
            }
        }
    }
}

@Composable
fun SafetyIndicator(safety: RouteSafety, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(safety.color).copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "SAFETY",
                style = MaterialTheme.typography.labelSmall,
                color = Color(safety.color)
            )
            Text(
                safety.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(safety.color)
            )
        }
    }
}

@Composable
fun TimeEstimate(time: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AccessTime,
                null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                time,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun RouteSection(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Navigation,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No Routes Calculated",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Enter start and destination to get AI-powered safe route recommendations",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Example routes
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "💡 Example Routes:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    """• Start: City Hall
  Destination: Emergency Shelter
  
• Start: Hospital District
  Destination: Safe Zone Alpha
  
• Start: Downtown Plaza
  Destination: Community Center""",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
