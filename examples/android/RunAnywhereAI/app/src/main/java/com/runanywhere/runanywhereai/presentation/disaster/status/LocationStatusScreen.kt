package com.runanywhere.runanywhereai.presentation.disaster.status

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationStatusScreen(
    navController: NavController
) {
    var selectedDisaster by remember { mutableStateOf("Select Disaster Type") }
    var selectedSeverity by remember { mutableStateOf("Select Severity") }
    var statusUpdated by remember { mutableStateOf(false) }
    var expandedDisaster by remember { mutableStateOf(false) }
    var expandedSeverity by remember { mutableStateOf(false) }

    val disasters = listOf(
        "🏚️ Earthquake",
        "🌊 Flood",
        "🔥 Fire",
        "🌪️ Tornado",
        "🌀 Hurricane",
        "⛈️ Storm",
        "🏔️ Landslide",
        "❄️ Blizzard",
        "🌡️ Heatwave",
        "💨 Wind Damage"
    )

    val severities = listOf(
        "🟢 Minimal - Minor issues",
        "🟡 Low - Some damage",
        "🟠 Moderate - Significant damage",
        "🔴 High - Severe damage",
        "🆘 Critical - Life-threatening"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📍 Location Status") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally),
                tint = Color(0xFFFF6F00)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Report Disaster Conditions",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Help your community by reporting local conditions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Disaster Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedDisaster,
                onExpandedChange = { expandedDisaster = it }
            ) {
                OutlinedTextField(
                    value = selectedDisaster,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Disaster Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDisaster)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedDisaster,
                    onDismissRequest = { expandedDisaster = false }
                ) {
                    disasters.forEach { disaster ->
                        DropdownMenuItem(
                            text = { Text(disaster) },
                            onClick = {
                                selectedDisaster = disaster
                                expandedDisaster = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Severity Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedSeverity,
                onExpandedChange = { expandedSeverity = it }
            ) {
                OutlinedTextField(
                    value = selectedSeverity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Severity Level") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSeverity)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedSeverity,
                    onDismissRequest = { expandedSeverity = false }
                ) {
                    severities.forEach { severity ->
                        DropdownMenuItem(
                            text = { Text(severity) },
                            onClick = {
                                selectedSeverity = severity
                                expandedSeverity = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (statusUpdated) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "✓ Status Updated",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Your community can now see current conditions in your area",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                Button(
                    onClick = { statusUpdated = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedDisaster != "Select Disaster Type" &&
                            selectedSeverity != "Select Severity"
                ) {
                    Text(
                        "UPDATE STATUS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "⚠️ Feature Coming Soon",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Real-time community status map with GPS location sharing",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}