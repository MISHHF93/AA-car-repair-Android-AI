package com.aa.carrepair.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PersonaSelection : Screen("persona_selection")
    data class Chat(val sessionId: String = "{sessionId}") : Screen("chat/{sessionId}") {
        companion object {
            fun createRoute(id: String) = "chat/$id"
        }
    }
    object EstimatorVehicle : Screen("estimator/vehicle")
    object EstimatorCategory : Screen("estimator/category")
    object EstimatorDiagnostic : Screen("estimator/diagnostic")
    object EstimatorResult : Screen("estimator/result")
    data class DtcAnalysis(val code: String = "{code}") : Screen("dtc/{code}") {
        companion object {
            fun createRoute(c: String) = "dtc/$c"
        }
    }
    data class Calculator(val type: String = "{type}") : Screen("calculator/{type}") {
        companion object {
            fun createRoute(t: String) = "calculator/$t"
        }
    }
    object CalculatorHub : Screen("calculators")
    object Fleet : Screen("fleet")
    object VoiceAssistant : Screen("voice")
    object Inspection : Screen("inspection")
    object Settings : Screen("settings")
    object Analytics : Screen("analytics")
}
