package com.aa.carrepair.feature.voice

data class VoiceUiState(
    val state: VoiceState = VoiceState.IDLE,
    val transcript: String = "",
    val response: String = "",
    val isOffline: Boolean = false,
    val error: String? = null,
    val waveformAmplitudes: List<Float> = emptyList()
)

enum class VoiceState {
    IDLE, LISTENING, PROCESSING, SPEAKING
}
