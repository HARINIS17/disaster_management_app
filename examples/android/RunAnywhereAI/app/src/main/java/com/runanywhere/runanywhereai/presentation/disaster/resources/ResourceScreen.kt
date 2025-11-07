package com.runanywhere.runanywhereai.presentation.disaster.resources

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * ⚕ Smart Resource Allocation
 * Predict urgent needs using on-device ML
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceScreen(
    viewModel: ResourceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val predictions by viewModel.predictions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("📦 Resource Allocator")
                        Text(
                            "AI-Powered Need Prediction",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
            // Input Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Disaster Situation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.situationDesc,
                        onValueChange = { viewModel.updateSituation(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Number of affected people") },
                        placeholder = { Text("e.g., 500") }
                    )

                    OutlinedTextField(
                        value = uiState.duration,
                        onValueChange = { viewModel.updateDuration(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Est. duration (days)") },
                        placeholder = { Text("e.g., 7") }
                    )

                    OutlinedTextField(
                        value = uiState.location,
                        onValueChange = { viewModel.updateLocation(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Location/Area") },
                        placeholder = { Text("e.g., Rural area, 3 villages") }
                    )

                    val errorMessage = uiState.error
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = { viewModel.predictResources() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isPredicting
                    ) {
                        if (uiState.isPredicting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Psychology, null)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Predict Resource Needs")
                    }
                }
            }

            // Predictions List
            if (predictions.isNotEmpty()) {
                Text(
                    "Resource Predictions",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(predictions) { prediction ->
                        ResourceCard(prediction)
                    }
                }
            } else {
                EmptyPredictionsMessage()
            }
        }
    }
}

@Composable
fun ResourceCard(prediction: ResourcePrediction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (prediction.priority) {
                "CRITICAL" -> MaterialTheme.colorScheme.errorContainer
                "HIGH" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${prediction.icon} ${prediction.category}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                PriorityBadge(prediction.priority)
            }

            Text(
                "Estimated: ${prediction.quantity}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                prediction.rationale,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val color = when (priority) {
        "CRITICAL" -> MaterialTheme.colorScheme.error
        "HIGH" -> Color(0xFFFF9800)
        "MEDIUM" -> Color(0xFF2196F3)
        else -> Color.Gray
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            priority,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun EmptyPredictionsMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Inventory,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No predictions yet",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Enter disaster details to get AI-powered resource predictions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class ResourceUiState(
    val situationDesc: String = "",
    val duration: String = "",
    val location: String = "",
    val isPredicting: Boolean = false,
    val error: String? = null
)

data class ResourcePrediction(
    val category: String,
    val quantity: String,
    val priority: String,
    val rationale: String,
    val icon: String
)
