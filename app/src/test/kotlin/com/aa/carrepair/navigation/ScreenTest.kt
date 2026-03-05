package com.aa.carrepair.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `SignIn route`() {
        assertEquals("sign_in", Screen.SignIn.route)
    }

    @Test
    fun `Home route`() {
        assertEquals("home", Screen.Home.route)
    }

    @Test
    fun `PersonaSelection route`() {
        assertEquals("persona_selection", Screen.PersonaSelection.route)
    }

    @Test
    fun `Chat route template`() {
        assertEquals("chat/{sessionId}", Screen.Chat().route)
    }

    @Test
    fun `Chat createRoute builds correct path`() {
        assertEquals("chat/abc-123", Screen.Chat.createRoute("abc-123"))
    }

    @Test
    fun `EstimatorVehicle route`() {
        assertEquals("estimator/vehicle", Screen.EstimatorVehicle.route)
    }

    @Test
    fun `EstimatorCategory route`() {
        assertEquals("estimator/category", Screen.EstimatorCategory.route)
    }

    @Test
    fun `EstimatorDiagnostic route`() {
        assertEquals("estimator/diagnostic", Screen.EstimatorDiagnostic.route)
    }

    @Test
    fun `EstimatorResult route`() {
        assertEquals("estimator/result", Screen.EstimatorResult.route)
    }

    @Test
    fun `DtcAnalysis route template`() {
        assertEquals("dtc/{code}", Screen.DtcAnalysis().route)
    }

    @Test
    fun `DtcAnalysis createRoute builds correct path`() {
        assertEquals("dtc/P0301", Screen.DtcAnalysis.createRoute("P0301"))
    }

    @Test
    fun `Calculator route template`() {
        assertEquals("calculator/{type}", Screen.Calculator().route)
    }

    @Test
    fun `Calculator createRoute builds correct path`() {
        assertEquals("calculator/oil_life", Screen.Calculator.createRoute("oil_life"))
    }

    @Test
    fun `CalculatorHub route`() {
        assertEquals("calculators", Screen.CalculatorHub.route)
    }

    @Test
    fun `Fleet route`() {
        assertEquals("fleet", Screen.Fleet.route)
    }

    @Test
    fun `VoiceAssistant route`() {
        assertEquals("voice", Screen.VoiceAssistant.route)
    }

    @Test
    fun `Inspection route`() {
        assertEquals("inspection", Screen.Inspection.route)
    }

    @Test
    fun `Settings route`() {
        assertEquals("settings", Screen.Settings.route)
    }

    @Test
    fun `Analytics route`() {
        assertEquals("analytics", Screen.Analytics.route)
    }
}
