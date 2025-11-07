package com.runanywhere.runanywhereai.presentation.disaster.damage

import androidx.compose.foundation.background
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
 * 📸 Damage Analyzer
 * AI-powered structural damage assessment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DamageScreen(
    viewModel: DamageViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val assessments by viewModel.assessments.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("📸 Damage Analyzer")
                        Text(
                            "AI Structural Assessment",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (assessments.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAssessments() }) {
                            Icon(Icons.Default.Delete, "Clear assessments")
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
            // Input Section
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
                        "Damage Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Structure Type Selector
                    Text(
                        "Structure Type",
                        style = MaterialTheme.typography.labelMedium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StructureType.values().take(3).forEach { type ->
                            FilterChip(
                                selected = uiState.structureType == type,
                                onClick = { viewModel.setStructureType(type) },
                                label = { Text("${type.icon} ${type.displayName}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StructureType.values().drop(3).forEach { type ->
                            FilterChip(
                                selected = uiState.structureType == type,
                                onClick = { viewModel.setStructureType(type) },
                                label = { Text("${type.icon} ${type.displayName}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Location
                    OutlinedTextField(
                        value = uiState.location,
                        onValueChange = { viewModel.updateLocation(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Location (optional)") },
                        placeholder = { Text("e.g., 123 Main St, Building A") },
                        enabled = !uiState.isAnalyzing
                    )

                    // Damage Description
                    OutlinedTextField(
                        value = uiState.damageDescription,
                        onValueChange = { viewModel.updateDescription(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        label = { Text("Damage Description *") },
                        placeholder = { Text("Describe visible damage: cracks, collapse, structural issues...") },
                        enabled = !uiState.isAnalyzing,
                        maxLines = 6
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

                    // Analyze Button
                    Button(
                        onClick = { viewModel.analyzeDamage() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isAnalyzing && uiState.damageDescription.isNotBlank()
                    ) {
                        if (uiState.isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Analytics, null, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(if (uiState.isAnalyzing) "Analyzing..." else "Analyze Damage")
                    }
                }
            }

            // Assessments List
            if (assessments.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(assessments) { assessment ->
                        AssessmentCard(assessment)
                    }
                }
            }
        }
    }
}

@Composable
fun AssessmentCard(assessment: DamageAssessment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(assessment.severity.color).copy(alpha = 0.1f)
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
                        assessment.structureType.icon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column {
                        Text(
                            assessment.structureType.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            assessment.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                PriorityBadge(assessment.priority)
            }

            HorizontalDivider()

            // Severity and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SeverityIndicator(assessment.severity, Modifier.weight(1f))
                StatusIndicator(assessment.structuralStatus, Modifier.weight(1f))
            }

            // Original Description
            DetailSection(
                title = "Description",
                content = assessment.description,
                icon = Icons.Default.Description
            )

            // Findings
            if (assessment.findings.isNotBlank()) {
                DetailSection(
                    title = "Key Findings",
                    content = assessment.findings,
                    icon = Icons.Default.Search
                )
            }

            // Safety Concerns (Highlighted)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                DetailSection(
                    title = "⚠️ Safety Concerns",
                    content = assessment.safetyConcerns,
                    icon = Icons.Default.Warning
                )
            }

            // Recommendations
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                DetailSection(
                    title = "✅ Recommended Actions",
                    content = assessment.recommendations,
                    icon = Icons.Default.CheckCircle
                )
            }

            // Casualties Estimate
            if (assessment.estimatedCasualties.isNotBlank() && 
                !assessment.estimatedCasualties.contains("Unable to determine")) {
                DetailSection(
                    title = "Casualty Estimate",
                    content = assessment.estimatedCasualties,
                    icon = Icons.Default.Person
                )
            }

            // Timestamp
            Text(
                "Assessment Time: ${assessment.timestamp}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SeverityIndicator(severity: SeverityLevel, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(severity.color).copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "SEVERITY",
                style = MaterialTheme.typography.labelSmall,
                color = Color(severity.color)
            )
            Text(
                severity.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(severity.color)
            )
        }
    }
}

@Composable
fun StatusIndicator(status: StructuralStatus, modifier: Modifier = Modifier) {
    val statusColor = when (status) {
        StructuralStatus.STABLE -> Color(0xFF4CAF50)
        StructuralStatus.COMPROMISED -> Color(0xFF2196F3)
        StructuralStatus.UNSAFE -> Color(0xFFFF9800)
        StructuralStatus.COLLAPSE_RISK -> Color(0xFFF44336)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = statusColor.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "STATUS",
                style = MaterialTheme.typography.labelSmall,
                color = statusColor
            )
            Text(
                status.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val color = when (priority) {
        "HIGH" -> Color(0xFFF44336)
        "LOW" -> Color(0xFF4CAF50)
        else -> Color(0xFF2196F3)
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
fun DetailSection(
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
            Icons.Default.PhotoCamera,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No Assessments Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Describe structural damage to get AI-powered safety assessment",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(24.dp))
        
        // Example descriptions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "💡 Example Descriptions:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    """• "Large vertical cracks in exterior walls, partial collapse of south wall, exposed rebar visible"
                    
• "Bridge deck showing severe cracking, concrete spalling, one support column tilted 15 degrees"
                    
• "Roof partially collapsed, structural beams sagging, windows shattered, foundation settling on east side"
""",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
