package com.aa.carrepair.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.aa.carrepair.feature.auth.PersonaSelectionScreen
import com.aa.carrepair.feature.auth.SignInScreen
import com.aa.carrepair.feature.calculators.CalculatorHubScreen
import com.aa.carrepair.feature.chat.ChatScreen
import com.aa.carrepair.feature.dtc.DtcScreen
import com.aa.carrepair.feature.estimator.EstimateResultScreen
import com.aa.carrepair.feature.estimator.DiagnosticChatScreen
import com.aa.carrepair.feature.estimator.ServiceCategoryScreen
import com.aa.carrepair.feature.estimator.VehicleIdScreen
import com.aa.carrepair.feature.fleet.FleetScreen
import com.aa.carrepair.feature.inspection.InspectionScreen
import com.aa.carrepair.feature.settings.SettingsScreen
import com.aa.carrepair.feature.voice.VoiceScreen
import com.aa.carrepair.ui.components.BottomNavBar
import com.aa.carrepair.feature.home.HomeScreen

private const val NAV_ANIMATION_DURATION = 300

/** Routes where the bottom navigation bar should be visible. */
private val BOTTOM_NAV_ROUTES = setOf(
    Screen.Home.route,
    Screen.Chat("").route,
    Screen.EstimatorVehicle.route,
    Screen.DtcAnalysis("").route,
    Screen.CalculatorHub.route,
)

@Composable
fun AppNavGraph(
    startDestination: String = Screen.Home.route,
    deepLinkVin: String? = null,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // If a deep-link VIN is present, override to the estimator start regardless of onboarding
    val resolvedStart = if (deepLinkVin != null) Screen.EstimatorVehicle.route else startDestination

    val showBottomBar = currentRoute in BOTTOM_NAV_ROUTES ||
        BOTTOM_NAV_ROUTES.any { currentRoute?.startsWith(it.substringBefore("{")) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = resolvedStart,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(NAV_ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(NAV_ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(NAV_ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(NAV_ANIMATION_DURATION)
                )
            }
        ) {
            // ── Onboarding ──────────────────────────────────────────────────
            composable(Screen.SignIn.route) {
                SignInScreen(
                    onGetStarted = {
                        navController.navigate(Screen.PersonaSelection.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.PersonaSelection.route) {
                PersonaSelectionScreen(
                    onPersonaSelected = {
                        navController.navigate(Screen.Home.route) {
                            // Clear the entire back stack so there is no way back to onboarding.
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ── Main Screens ─────────────────────────────────────────────────
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToChat = { sessionId ->
                        navController.navigate(Screen.Chat.createRoute(sessionId))
                    },
                    onNavigateToEstimator = {
                        navController.navigate(Screen.EstimatorVehicle.route)
                    },
                    onNavigateToDtc = {
                        navController.navigate(Screen.DtcAnalysis.createRoute(""))
                    },
                    onNavigateToCalculators = {
                        navController.navigate(Screen.CalculatorHub.route)
                    },
                    onNavigateToFleet = {
                        navController.navigate(Screen.Fleet.route)
                    },
                    onNavigateToVoice = {
                        navController.navigate(Screen.VoiceAssistant.route)
                    },
                    onNavigateToInspection = {
                        navController.navigate(Screen.Inspection.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(
                route = Screen.Chat("").route,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: "new"
                ChatScreen(
                    sessionId = sessionId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.EstimatorVehicle.route) {
                VehicleIdScreen(
                    onVehicleIdentified = {
                        navController.navigate(Screen.EstimatorCategory.route)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.EstimatorCategory.route) {
                ServiceCategoryScreen(
                    onCategorySelected = {
                        navController.navigate(Screen.EstimatorDiagnostic.route)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.EstimatorDiagnostic.route) {
                DiagnosticChatScreen(
                    onEstimateReady = {
                        navController.navigate(Screen.EstimatorResult.route)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.EstimatorResult.route) {
                EstimateResultScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToChat = { sessionId ->
                        navController.navigate(Screen.Chat.createRoute(sessionId))
                    }
                )
            }

            composable(
                route = Screen.DtcAnalysis("").route,
                arguments = listOf(navArgument("code") {
                    type = NavType.StringType
                    defaultValue = ""
                }),
                deepLinks = listOf(navDeepLink { uriPattern = "vin://dtc/{code}" })
            ) { backStackEntry ->
                val code = backStackEntry.arguments?.getString("code") ?: ""
                DtcScreen(
                    initialCode = code,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CalculatorHub.route) {
                CalculatorHubScreen(
                    onCalculatorSelected = { type ->
                        navController.navigate(Screen.Calculator.createRoute(type))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Calculator("").route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->
                val calcType = backStackEntry.arguments?.getString("type") ?: ""
                com.aa.carrepair.feature.calculators.CalculatorScreen(
                    calculatorType = calcType,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Fleet.route) {
                FleetScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.VoiceAssistant.route) {
                VoiceScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Inspection.route) {
                InspectionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPersonaSelection = {
                        navController.navigate(Screen.PersonaSelection.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}
