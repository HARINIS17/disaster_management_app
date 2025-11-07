package com.runanywhere.runanywhereai.presentation.disaster.summarizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 🧾 Situation Summarizer
 * Convert field notes to structured mission reports offline
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummarizerScreen(
    viewModel: SummarizerViewModel = viewModel(
        factory = SummarizerViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val reports by viewModel.reports.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🧾 Situation Summarizer")
                        Text(
                            "Field Notes → Mission Reports",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearReports() }) {
                        Icon(Icons.Default.Delete, "Clear reports")
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
            // Report Type Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Report Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ReportTypeSelector(
                        selectedType = uiState.reportType,
                        onTypeSelected = { viewModel.setReportType(it) }
                    )
                }
            }

            // Input Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Field Notes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.fieldNotes,
                        onValueChange = { viewModel.updateFieldNotes(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = { Text("Enter field observations, damage reports, casualty info...") },
                        enabled = !uiState.isGenerating
                    )

                    Button(
                        onClick = { viewModel.generateReport() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isGenerating && uiState.fieldNotes.isNotBlank()
                    ) {
                        if (uiState.isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Description, null)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Generate Report")
                    }
                }
            }

            // Generated Reports
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (reports.isEmpty()) {
                    item {
                        EmptyReportsMessage()
                    }
                } else {
                    items(reports) { report ->
                        ReportCard(report, onCopy = { viewModel.copyReport(report) })
                    }
                }
            }
        }
    }
}

@Composable
fun ReportTypeSelector(
    selectedType: ReportType,
    onTypeSelected: (ReportType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReportType.values().forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text("${type.icon} ${type.displayName}") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ReportCard(
    report: DisasterReport,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${report.type.icon} ${report.type.displayName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, "Copy report")
                }
            }

            Divider()

            Text(
                report.content,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                report.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyReportsMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "No reports generated yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Enter field notes and generate a structured mission report",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Example field notes
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "💡 Example Field Notes:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    """• "Building collapse at 5th and Main. 3 injured, 1 critical. Need medical support and excavation equipment urgently."
                    
• "Flood waters rising rapidly. 50+ families evacuated to community center. Need food, water, blankets."
                    
• "Power outage affecting downtown area. Traffic lights down. 2 vehicle accidents reported.""",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Data classes
enum class ReportType(val displayName: String, val icon: String) {
    SITREP("Situation Report", "📋"),
    CASUALTY("Casualty Report", "🏥"),
    DAMAGE("Damage Assessment", "🏚️"),
    RESOURCE("Resource Request", "📦"),
    INCIDENT("Incident Report", "⚠️")
}

data class DisasterReport(
    val type: ReportType,
    val content: String,
    val timestamp: String
)
