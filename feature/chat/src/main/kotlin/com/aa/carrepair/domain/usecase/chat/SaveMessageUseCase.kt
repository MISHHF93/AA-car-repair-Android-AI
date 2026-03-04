package com.aa.carrepair.domain.usecase.chat

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.repository.ChatRepository
import javax.inject.Inject

class SaveMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(message: ChatMessage): DataResult<Unit> =
        chatRepository.saveMessage(message)
}
