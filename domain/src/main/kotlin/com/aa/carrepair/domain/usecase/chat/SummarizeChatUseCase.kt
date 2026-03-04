package com.aa.carrepair.domain.usecase.chat

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.repository.ChatRepository
import javax.inject.Inject

class SummarizeChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(sessionId: String): DataResult<String> =
        chatRepository.summarizeSession(sessionId)
}
