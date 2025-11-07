package com.runanywhere.runanywhereai.presentation.disaster

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class DisasterFeature(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val gradient: List<Color>,
    val priority: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisasterDashboardScreen(
    navController: NavController
) {
    val features = remember {
        listOf(
            DisasterFeature(
                id = "alert",
                title = "Emergency Alert",
                description = "Broadcast SOS to nearby users",
                icon = Icons.Default.Warning,
                route = "emergency_alert",
                gradient = listOf(Color(0xFFE53935), Color(0xFFB71C1C)),
                priority = "CRITICAL"
            ),
            DisasterFeature(
                id = "status",
                title = "Location Status",
                description = "Report disaster conditions",
                icon = Icons.Default.LocationOn,
                route = "location_status",
                gradient = listOf(Color(0xFFFF6F00), Color(0xFFE65100)),
                priority = "HIGH"
            ),
            DisasterFeature(
                id = "translation",
                title = "Language Translation",
                description = "Translate speech/text between languages offline",
                icon = Icons.Default.Translate,
                route = "disaster/translation",
                gradient = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
                priority = "CRITICAL"
            ),
            DisasterFeature(
                id = "summarizer",
                title = "Situation Summarizer",
                description = "Convert field notes to mission reports",
                icon = Icons.Default.Description,
                route = "disaster/summarizer",
                gradient = listOf(Color(0xFFf093fb), Color(0xFFf5576c)),
                priority = "HIGH"
            ),
            DisasterFeature(
                id = "resources",
                title = "Resource Allocation",
                description = "Predict urgent needs using AI",
                icon = Icons.Default.Inventory,
                route = "disaster/resources",
                gradient = listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
                priority = "HIGH"
            ),
            DisasterFeature(
                id = "damage",
                title = "Damage Analyzer",
                description = "Classify damage from images offline",
                icon = Icons.Default.PhotoCamera,
                route = "disaster/damage",
                gradient = listOf(Color(0xFFfa709a), Color(0xFFfee140)),
                priority = "MEDIUM"
            ),
            DisasterFeature(
                id = "navigation",
                title = "Safe Routes",
                description = "Navigate without internet",
                icon = Icons.Default.Navigation,
                route = "disaster/navigation",
                gradient = listOf(Color(0xFF30cfd0), Color(0xFF330867)),
                priority = "HIGH"
            ),
            DisasterFeature(
                id = "emergency",
                title = "Emergency Assistant",
                description = "First-aid & survival tips offline",
                icon = Icons.Default.MedicalServices,
                route = "disaster/emergency",
                gradient = listOf(Color(0xFFa8edea), Color(0xFFfed6e3)),
                priority = "CRITICAL"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Disaster Response",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "100% Offline • Privacy-First AI",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Status indicator
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4CAF50), shape = RoundedCornerShape(4.dp))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "OFFLINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Emergency Banner
            EmergencyBanner()

            Spacer(modifier = Modifier.height(16.dp))

            // Features Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(features) { feature ->
                    FeatureCard(
                        feature = feature,
                        onClick = {
                            navController.navigate(feature.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Emergency Mode Active",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    "All AI processing happens on-device. No internet required.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: DisasterFeature,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = feature.gradient
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Priority badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.3f)
                ) {
                    Text(
                        feature.priority,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Icon
                Icon(
                    feature.icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )

                Spacer(Modifier.weight(1f))

                // Title and description
                Column {
                    Text(
                        feature.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        feature.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 2
                    )
                }
            }
        }
    }
}
