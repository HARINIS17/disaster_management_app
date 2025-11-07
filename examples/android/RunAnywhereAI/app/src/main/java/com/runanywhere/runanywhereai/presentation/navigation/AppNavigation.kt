package com.runanywhere.runanywhereai.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.runanywhere.runanywhereai.presentation.chat.ChatScreen
import com.runanywhere.runanywhereai.presentation.models.ModelsScreen
import com.runanywhere.runanywhereai.presentation.disaster.DisasterDashboardScreen
import com.runanywhere.runanywhereai.presentation.disaster.translation.TranslationScreen
import com.runanywhere.runanywhereai.presentation.disaster.summarizer.SummarizerScreen
import com.runanywhere.runanywhereai.presentation.disaster.resources.ResourceScreen
import com.runanywhere.runanywhereai.presentation.disaster.damage.DamageScreen
import com.runanywhere.runanywhereai.presentation.disaster.navigation.SafeRoutesScreen
import com.runanywhere.runanywhereai.presentation.disaster.emergency.EmergencyScreen
import com.runanywhere.runanywhereai.presentation.disaster.emergency.EmergencyAlertScreen
import com.runanywhere.runanywhereai.presentation.disaster.status.LocationStatusScreen

/**
 * Main navigation component for RunAnywhere AI Demo App
 * Includes: Chat, Models, and Disaster Response features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavigationRoute.CHAT,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Main App Routes
            composable(NavigationRoute.CHAT) {
                ChatScreen()
            }

            composable(NavigationRoute.MODELS) {
                ModelsScreen()
            }

            composable(NavigationRoute.DISASTER) {
                DisasterDashboardScreen(navController = navController)
            }

            // Disaster Response Feature Routes
            composable("disaster/translation") {
                TranslationScreen()
            }

            composable("disaster/summarizer") {
                SummarizerScreen()
            }

            composable("disaster/resources") {
                ResourceScreen()
            }

            composable("disaster/damage") {
                DamageScreen()
            }

            composable("disaster/navigation") {
                SafeRoutesScreen()
            }

            composable("disaster/emergency") {
                EmergencyScreen()
            }

            composable("emergency_alert") {
                EmergencyAlertScreen(navController = navController)
            }

            composable("location_status") {
                LocationStatusScreen(navController = navController)
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, description: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Filled.Construction,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem(
            route = NavigationRoute.CHAT,
            label = "Chat",
            icon = Icons.Outlined.Chat,
            selectedIcon = Icons.Filled.Chat
        ),
        BottomNavItem(
            route = NavigationRoute.MODELS,
            label = "Models",
            icon = Icons.Outlined.Storage,
            selectedIcon = Icons.Filled.Storage
        ),
        BottomNavItem(
            route = NavigationRoute.DISASTER,
            label = "Disaster",
            icon = Icons.Outlined.Warning,
            selectedIcon = Icons.Filled.Warning
        )
    )

    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Navigation routes for app features
 */
object NavigationRoute {
    const val CHAT = "chat"
    const val MODELS = "models"
    const val DISASTER = "disaster"
}

/**
 * Bottom navigation item data
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)
