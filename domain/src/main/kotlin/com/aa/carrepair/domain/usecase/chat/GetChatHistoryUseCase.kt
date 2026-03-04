package com.aa.carrepair.domain.usecase.chat

import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(sessionId: String): Flow<List<ChatMessage>> =
        chatRepository.getChatSession(sessionId)
}
