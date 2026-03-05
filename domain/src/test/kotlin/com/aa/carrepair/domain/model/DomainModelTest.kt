package com.aa.carrepair.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainModelTest {

    // ── Vehicle ─────────────────────────────────────────────────────────

    @Test
    fun `Vehicle displayName combines year make model`() {
        val vehicle = Vehicle(
            id = "v1", vin = "1HGBH41JXMN109186",
            year = 2021, make = "Honda", model = "Civic"
        )
        assertEquals("2021 Honda Civic", vehicle.displayName)
    }

    @Test
    fun `Vehicle defaults are null`() {
        val vehicle = Vehicle(id = "v1", vin = "VIN", year = 2020, make = "Toyota", model = "Camry")
        assertEquals(null, vehicle.engine)
        assertEquals(null, vehicle.trim)
        assertEquals(null, vehicle.mileage)
        assertEquals(null, vehicle.color)
    }

    // ── SafetyLevel ─────────────────────────────────────────────────────

    @Test
    fun `CRITICAL is not drivable`() {
        assertFalse(SafetyLevel.CRITICAL.isDrivable)
    }

    @Test
    fun `HIGH is drivable`() {
        assertTrue(SafetyLevel.HIGH.isDrivable)
    }

    @Test
    fun `MEDIUM is drivable`() {
        assertTrue(SafetyLevel.MEDIUM.isDrivable)
    }

    @Test
    fun `LOW is drivable`() {
        assertTrue(SafetyLevel.LOW.isDrivable)
    }

    // ── UserPersona ─────────────────────────────────────────────────────

    @Test
    fun `DIY_OWNER displayName`() {
        assertEquals("DIY Vehicle Owner", UserPersona.DIY_OWNER.displayName)
    }

    @Test
    fun `PROFESSIONAL_TECHNICIAN displayName`() {
        assertEquals("Professional Technician", UserPersona.PROFESSIONAL_TECHNICIAN.displayName)
    }

    @Test
    fun `FLEET_MANAGER displayName`() {
        assertEquals("Fleet / Service Manager", UserPersona.FLEET_MANAGER.displayName)
    }

    // ── ChatMessage ─────────────────────────────────────────────────────

    @Test
    fun `ChatMessage defaults`() {
        val msg = ChatMessage(
            id = "m1", sessionId = "s1",
            content = "Hello", role = MessageRole.USER
        )
        assertEquals(AgentType.GENERAL, msg.agentType)
        assertEquals(null, msg.confidence)
        assertEquals(null, msg.safetyLevel)
        assertEquals(null, msg.attachmentUri)
    }

    @Test
    fun `MessageRole has three values`() {
        assertEquals(3, MessageRole.values().size)
    }

    @Test
    fun `AgentType has four values`() {
        assertEquals(4, AgentType.values().size)
    }

    // ── Part ────────────────────────────────────────────────────────────

    @Test
    fun `Part selectedPrice returns OEM when isOem true`() {
        val part = Part(
            partNumber = "P1", name = "Brake Pad",
            oemPrice = 50.0, aftermarketPrice = 30.0,
            availability = "In Stock", isOem = true
        )
        assertEquals(50.0, part.selectedPrice, 0.01)
    }

    @Test
    fun `Part selectedPrice returns aftermarket when isOem false`() {
        val part = Part(
            partNumber = "P1", name = "Brake Pad",
            oemPrice = 50.0, aftermarketPrice = 30.0,
            availability = "In Stock", isOem = false
        )
        assertEquals(30.0, part.selectedPrice, 0.01)
    }

    @Test
    fun `Part selectedPrice falls back to OEM when aftermarket is null`() {
        val part = Part(
            partNumber = "P1", name = "Brake Pad",
            oemPrice = 50.0, aftermarketPrice = null,
            availability = "In Stock", isOem = false
        )
        assertEquals(50.0, part.selectedPrice, 0.01)
    }

    // ── CalculatorType ──────────────────────────────────────────────────

    @Test
    fun `CalculatorType has 14 types`() {
        assertEquals(14, CalculatorType.values().size)
    }

    // ── FleetVehicleStatus ──────────────────────────────────────────────

    @Test
    fun `FleetVehicleStatus has 4 statuses`() {
        assertEquals(4, FleetVehicleStatus.values().size)
    }

    // ── AgentResponse ───────────────────────────────────────────────────

    @Test
    fun `AgentResponse defaults`() {
        val response = AgentResponse(
            content = "ok", agentType = AgentType.GENERAL,
            confidence = 90, safetyAssessment = null
        )
        assertEquals(emptyList<String>(), response.suggestedActions)
        assertEquals(emptyMap<String, String>(), response.metadata)
    }

    // ── DtcCode ─────────────────────────────────────────────────────────

    @Test
    fun `DtcCode defaults for optional fields`() {
        val dtc = DtcCode(
            code = "P0301", definition = "Misfire",
            system = "Powertrain", causes = emptyList(),
            symptoms = emptyList(), repairProcedures = emptyList(),
            safetyLevel = SafetyLevel.HIGH, confidenceScore = 85
        )
        assertEquals(emptyList<String>(), dtc.relatedCodes)
        assertEquals(emptyList<RepairHistoryEntry>(), dtc.repairHistory)
    }

    // ── SafetyClassification ────────────────────────────────────────────

    @Test
    fun `SafetyClassification is a data class with correct fields`() {
        val classification = SafetyClassification(
            level = SafetyLevel.CRITICAL,
            triggers = listOf("brake failure"),
            recommendedAction = "Stop now",
            isDrivable = false
        )
        assertEquals(SafetyLevel.CRITICAL, classification.level)
        assertEquals(1, classification.triggers.size)
        assertFalse(classification.isDrivable)
    }
}
