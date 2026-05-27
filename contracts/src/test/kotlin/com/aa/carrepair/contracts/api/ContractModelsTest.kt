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
    fun `AgentChatRequest holds mobile chat contract`() {
        val request = AgentChatRequest(
            requestId = "req-1",
            timestampUtc = "2026-05-27T16:00:00Z",
            surface = "mobile",
            userRole = "consumer",
            locale = "en-CA",
            queryText = "P0171",
            policyProfile = "mobile_default",
            privacyMode = "standard"
        )

        assertEquals("req-1", request.requestId)
        assertEquals("mobile", request.surface)
        assertEquals("consumer", request.userRole)
        assertEquals("en-CA", request.locale)
        assertEquals("P0171", request.queryText)
        assertEquals("mobile_default", request.policyProfile)
        assertEquals("standard", request.privacyMode)
    }

    @Test
    fun `AgentChatResponse holds copilot response fields`() {
        val response = AgentChatResponse(
            answerText = "Check your intake for vacuum leaks.",
            confidence = 85,
            safetyLevel = "LOW",
            citations = listOf("AA-DTC-P0171"),
            auditTraceId = "audit-1"
        )

        assertEquals("Check your intake for vacuum leaks.", response.answerText)
        assertEquals(85, response.confidence)
        assertEquals("LOW", response.safetyLevel)
        assertEquals(1, response.citations.size)
        assertEquals("audit-1", response.auditTraceId)
        assertEquals("mobile", response.surface)
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
