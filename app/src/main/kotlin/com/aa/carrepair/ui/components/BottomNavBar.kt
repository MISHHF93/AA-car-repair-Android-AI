package com.aa.carrepair.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavController
import com.aa.carrepair.R
import com.aa.carrepair.navigation.Screen

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, Icons.Default.Home, R.string.nav_home),
        BottomNavItem(Screen.Chat.createRoute("new"), Icons.Default.Chat, R.string.nav_chat),
        BottomNavItem(Screen.EstimatorVehicle.route, Icons.Default.Build, R.string.nav_estimator),
        BottomNavItem(Screen.DtcAnalysis.createRoute(""), Icons.Default.Search, R.string.nav_dtc),
        BottomNavItem(Screen.CalculatorHub.route, Icons.Default.Calculate, R.string.nav_calculators),
    )

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route ||
                currentRoute?.startsWith(item.route.substringBefore("/")) == true
            val label = stringResource(id = item.labelResId)
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = label,
                        modifier = androidx.compose.ui.Modifier.semantics {
                            contentDescription = label
                        }
                    )
                },
                label = { Text(text = label) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelResId: Int
)
