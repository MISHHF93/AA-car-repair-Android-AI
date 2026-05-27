package com.aa.carrepair.feature.chat

import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.ObdContext
import com.aa.carrepair.domain.model.VehicleContext

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val currentAgentType: AgentType = AgentType.GENERAL,
    val inputText: String = "",
    val error: String? = null,
    val sessionId: String = "",
    val isVehicleContextExpanded: Boolean = false,
    val vehicleVin: String = "",
    val vehicleYear: String = "",
    val vehicleMake: String = "",
    val vehicleModel: String = "",
    val vehicleMileage: String = "",
    val isObdContextExpanded: Boolean = false,
    val obdDtcCode: String = "",
    val obdPendingCodes: String = "",
    val obdFreezeFrameSummary: String = "",
    val obdLivePidSummary: String = ""
) {
    fun vehicleContextOrNull(): VehicleContext? {
        val context = VehicleContext(
            vin = vehicleVin.trim().takeIf { it.isNotBlank() },
            year = vehicleYear.trim().toIntOrNull(),
            make = vehicleMake.trim().takeIf { it.isNotBlank() },
            model = vehicleModel.trim().takeIf { it.isNotBlank() },
            mileageKm = vehicleMileage.trim().toIntOrNull()
        )
        return context.takeIf { it.hasAnyValue }
    }

    fun obdContextOrNull(): ObdContext? {
        val primaryCodes = obdDtcCode.split(",", " ", "\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val context = ObdContext(
            dtcCodes = primaryCodes,
            pendingCodes = obdPendingCodes.split(",", " ", "\n")
                .map { it.trim() }
                .filter { it.isNotBlank() },
            freezeFrame = obdFreezeFrameSummary.trim()
                .takeIf { it.isNotBlank() }
                ?.let { mapOf("summary" to it) }
                .orEmpty(),
            livePids = obdLivePidSummary.trim()
                .takeIf { it.isNotBlank() }
                ?.let { mapOf("summary" to it) }
                .orEmpty()
        )
        return context.takeIf { it.hasAnyValue }
    }
}
