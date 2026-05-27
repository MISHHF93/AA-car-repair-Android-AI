package com.aa.carrepair.domain.model

data class ObdContext(
    val dtcCodes: List<String> = emptyList(),
    val pendingCodes: List<String> = emptyList(),
    val freezeFrame: Map<String, String> = emptyMap(),
    val livePids: Map<String, String> = emptyMap()
) {
    val hasAnyValue: Boolean
        get() = dtcCodes.isNotEmpty() ||
            pendingCodes.isNotEmpty() ||
            freezeFrame.isNotEmpty() ||
            livePids.isNotEmpty()
}
