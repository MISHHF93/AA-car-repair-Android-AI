package com.aa.carrepair.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.AgentResponse
import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import com.aa.carrepair.domain.usecase.chat.GetChatHistoryUseCase
import com.aa.carrepair.domain.usecase.chat.SaveMessageUseCase
import com.aa.carrepair.domain.usecase.chat.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun initSession(sessionId: String) {
        val id = if (sessionId == "new") UUID.randomUUID().toString() else sessionId
        _uiState.update { it.copy(sessionId = id) }
        observeChatHistory(id)
    }

    private fun observeChatHistory(sessionId: String) {
        getChatHistoryUseCase(sessionId)
            .onEach { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
            .launchIn(viewModelScope)
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage(vehicleVin: String? = null) {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        val sessionId = _uiState.value.sessionId

        _uiState.update { it.copy(inputText = "", isTyping = true, error = null) }

        viewModelScope.launch {
            when (val result = sendMessageUseCase(sessionId, text, vehicleVin)) {
                is DataResult.Success -> {
                    val response = result.data
                    _uiState.update { state ->
                        state.copy(
                            isTyping = false,
                            currentAgentType = response.agentType
                        )
                    }
                }
                is DataResult.Error -> {
                    Timber.e(result.exception, "Failed to send message")
                    _uiState.update { it.copy(isTyping = false, error = "Failed to send message. Please try again.") }
                }
                is DataResult.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
