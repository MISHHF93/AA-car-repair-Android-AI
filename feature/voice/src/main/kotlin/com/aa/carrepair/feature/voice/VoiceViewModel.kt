package com.aa.carrepair.feature.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class VoiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor
) : ViewModel(), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context, this)
        networkMonitor.isOnline
            .onEach { online -> _uiState.update { it.copy(isOffline = !online) } }
            .launchIn(viewModelScope)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            Timber.d("TTS initialized successfully")
        }
    }

    fun startListening() {
        _uiState.update { it.copy(state = VoiceState.LISTENING, transcript = "", error = null) }
    }

    fun stopListening() {
        if (_uiState.value.state == VoiceState.LISTENING) {
            _uiState.update { it.copy(state = VoiceState.IDLE) }
        }
    }

    fun onTranscriptReceived(transcript: String) {
        _uiState.update { it.copy(transcript = transcript, state = VoiceState.PROCESSING) }
        processCommand(transcript)
    }

    private fun processCommand(command: String) {
        val lower = command.lowercase()
        val response = when {
            lower.contains("oil") && lower.contains("change") ->
                "Oil change is typically recommended every 5,000 to 7,500 miles for synthetic oil."
            lower.contains("tire") && lower.contains("pressure") ->
                "Most passenger vehicles require 30 to 35 PSI. Check your door jamb sticker for exact specifications."
            lower.contains("check engine") ->
                "A check engine light can indicate many issues. I recommend scanning for DTC codes to get a precise diagnosis."
            lower.contains("brake") ->
                "Brake pads typically need replacement every 25,000 to 65,000 miles depending on driving style."
            else -> "I can help with car repair questions. Try asking about oil changes, tire pressure, or diagnostic codes."
        }
        _uiState.update { it.copy(response = response, state = VoiceState.SPEAKING) }
        speak(response)
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_id")
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }
}
