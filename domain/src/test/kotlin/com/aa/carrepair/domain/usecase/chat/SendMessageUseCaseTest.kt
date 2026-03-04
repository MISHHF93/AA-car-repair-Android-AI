package com.aa.carrepair.domain.usecase.chat

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.AgentResponse
import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SendMessageUseCaseTest {

    private val chatRepository: ChatRepository = mockk(relaxed = true)
    private lateinit var useCase: SendMessageUseCase

    @Before
    fun setUp() {
        useCase = SendMessageUseCase(chatRepository)
    }

    @Test
    fun `invoke saves user message and calls sendMessage`() = runTest {
        val expectedResponse = AgentResponse(
            content = "Here is the repair info",
            agentType = AgentType.DIAGNOSIS,
            confidence = 85,
            safetyAssessment = null
        )
        coEvery { chatRepository.sendMessage(any(), any(), any()) } returns DataResult.Success(expectedResponse)
        coEvery { chatRepository.saveMessage(any()) } returns DataResult.Success(Unit)

        val result = useCase("session_1", "My brakes are squeaking")

        assertTrue(result is DataResult.Success)
        assertEquals(expectedResponse, (result as DataResult.Success).data)
        coVerify { chatRepository.saveMessage(any()) }
        coVerify { chatRepository.sendMessage("session_1", "My brakes are squeaking", null) }
    }

    @Test
    fun `invoke propagates error from repository`() = runTest {
        coEvery { chatRepository.saveMessage(any()) } returns DataResult.Success(Unit)
        coEvery { chatRepository.sendMessage(any(), any(), any()) } returns
            DataResult.Error(RuntimeException("Network error"))

        val result = useCase("session_1", "Hello")

        assertTrue(result is DataResult.Error)
    }
}
