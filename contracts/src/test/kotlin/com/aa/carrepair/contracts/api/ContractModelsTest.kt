package com.aa.carrepair.contracts.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class ContractModelsTest {

    // --- VehicleApiContract ---

    @Test
    fun `VehicleDto holds all fields`() {
        val dto = VehicleDto(
            vin = "1HGCM82633A004352",
            year = 2020,
            make = "Honda",
            model = "Accord",
            engine = "2.0L Turbo",
            trim = "Sport",
            transmission = "CVT",
            driveType = "FWD",
            fuelType = "Gasoline",
            bodyStyle = "Sedan"
        )
        assertEquals("1HGCM82633A004352", dto.vin)
        assertEquals(2020, dto.year)
        assertEquals("Honda", dto.make)
        assertEquals("Sedan", dto.bodyStyle)
    }

    @Test
    fun `VehicleDto nullable fields default to null when not provided`() {
        val dto = VehicleDto(
            vin = "VIN1", year = 2022, make = "Ford", model = "F-150",
            engine = null, trim = null, transmission = null,
            driveType = null, fuelType = null, bodyStyle = null
        )
        assertNull(dto.engine)
        assertNull(dto.trim)
        assertNull(dto.bodyStyle)
    }

    @Test
    fun `VinDecodeResponse holds vehicle and validity`() {
        val vehicle = VehicleDto("VIN1", 2020, "Toyota", "Camry", null, null, null, null, null, null)
        val response = VinDecodeResponse(vin = "VIN1", vehicle = vehicle, isValid = true)
        assertTrue(response.isValid)
        assertEquals("Toyota", response.vehicle.make)
        assertNull(response.error)
    }

    @Test
    fun `VinDecodeResponse with error`() {
        val vehicle = VehicleDto("BAD", 0, "", "", null, null, null, null, null, null)
        val response = VinDecodeResponse(vin = "BAD", vehicle = vehicle, isValid = false, error = "Invalid VIN")
        assertFalse(response.isValid)
        assertEquals("Invalid VIN", response.error)
    }

    // --- AgentApiContract ---

    @Test
    fun `AgentChatRequest defaults`() {
        val request = AgentChatRequest(sessionId = "s1", message = "Hello")
        assertEquals("s1", request.sessionId)
        assertEquals("Hello", request.message)
        assertNull(request.agentType)
        assertNull(request.persona)
        assertTrue(request.context.isEmpty())
        assertNull(request.vehicleVin)
    }

    @Test
    fun `AgentChatRequest with all fields`() {
        val context = listOf(MessageContext(role = "user", content = "Previous message"))
        val request = AgentChatRequest(
            sessionId = "s1", message = "Help", agentType = "DIAGNOSIS",
            persona = "DIY_OWNER", context = context, vehicleVin = "VIN123"
        )
        assertEquals("DIAGNOSIS", request.agentType)
        assertEquals(1, request.context.size)
        assertEquals("VIN123", request.vehicleVin)
    }

    @Test
    fun `AgentChatResponse holds response fields`() {
        val response = AgentChatResponse(
            sessionId = "s1", messageId = "m1", content = "Check your oil",
            agentType = "GENERAL", confidence = 85, safetyLevel = "LOW",
            suggestedActions = listOf("Schedule service"),
            metadata = mapOf("source" to "kb")
        )
        assertEquals("Check your oil", response.content)
        assertEquals(85, response.confidence)
        assertEquals(1, response.suggestedActions.size)
        assertEquals("kb", response.metadata["source"])
    }

    @Test
    fun `AgentDiagnoseRequest defaults`() {
        val request = AgentDiagnoseRequest(symptoms = listOf("noise when braking"))
        assertEquals(1, request.symptoms.size)
        assertTrue(request.dtcCodes.isEmpty())
        assertNull(request.vehicleVin)
        assertNull(request.mileage)
    }

    // --- DtcApiContract ---

    @Test
    fun `DtcAnalysisResponse holds all fields`() {
        val cause = DtcCauseDto(cause = "Bad sensor", probability = 0.8, description = "O2 sensor failure")
        val history = RepairHistoryDto(repair = "Replace O2 sensor", successRate = 0.95, avgCost = 250.0, occurrences = 42)
        val response = DtcAnalysisResponse(
            code = "P0420", definition = "Catalyst efficiency below threshold",
            system = "Powertrain", causes = listOf(cause),
            symptoms = listOf("Check engine light"), repairProcedures = listOf("Replace catalytic converter"),
            safetyLevel = "MEDIUM", confidenceScore = 78,
            relatedCodes = listOf("P0430"), repairHistory = listOf(history)
        )
        assertEquals("P0420", response.code)
        assertEquals(1, response.causes.size)
        assertEquals(0.8, response.causes[0].probability, 0.01)
        assertEquals(42, response.repairHistory[0].occurrences)
    }

    // --- EstimatorApiContract ---

    @Test
    fun `EstimateRequest defaults`() {
        val request = EstimateRequest(
            vehicleVin = "VIN1", serviceCategory = "Brakes", description = "squeaking"
        )
        assertNull(request.mileage)
        assertTrue(request.preferOem)
        assertNull(request.zipCode)
    }

    @Test
    fun `EstimateResponse totals are consistent`() {
        val part = PartDto("BP-001", "Brake Pad", 45.0, 30.0, "In Stock")
        val labor = LaborItemDto("Install pads", 1.5, 80.0, 120.0)
        val vehicle = VehicleDto("VIN1", 2020, "Honda", "Accord", null, null, null, null, null, null)
        val response = EstimateResponse(
            estimateId = "e1", vehicle = vehicle, serviceCategory = "Brakes",
            parts = listOf(part), laborItems = listOf(labor),
            subtotalParts = 45.0, subtotalLabor = 120.0,
            fees = 5.0, tax = 13.6, total = 183.6,
            confidence = 82, disclaimer = "Estimate only"
        )
        assertEquals(183.6, response.total, 0.01)
        assertEquals("BP-001", response.parts[0].partNumber)
        assertEquals(1.5, response.laborItems[0].hours, 0.01)
    }

    @Test
    fun `PartDto default isOem is true`() {
        val part = PartDto("P1", "Pad", 50.0, 35.0, "Available")
        assertTrue(part.isOem)
    }

    // --- InspectionApiContract ---

    @Test
    fun `InspectionResponse holds findings`() {
        val bbox = BoundingBoxDto(0.1f, 0.2f, 0.8f, 0.9f)
        val finding = InspectionFindingDto(
            type = "Dent", description = "Large dent on hood",
            severity = "HIGH", confidence = 0.88, boundingBox = bbox
        )
        val response = InspectionResponse(
            inspectionId = "i1", mode = "DAMAGE_ASSESSMENT",
            findings = listOf(finding), severityScore = 7.5,
            summary = "Significant damage", recommendations = listOf("Body repair")
        )
        assertEquals(1, response.findings.size)
        assertEquals(0.1f, response.findings[0].boundingBox!!.left)
        assertEquals(7.5, response.severityScore, 0.01)
    }

    @Test
    fun `InspectionFindingDto with null boundingBox`() {
        val finding = InspectionFindingDto(
            type = "Rust", description = "Surface rust on rocker panel",
            severity = "MEDIUM", confidence = 0.75, boundingBox = null
        )
        assertNull(finding.boundingBox)
    }
}
