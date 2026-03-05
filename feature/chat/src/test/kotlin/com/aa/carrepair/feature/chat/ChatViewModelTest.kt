package com.aa.carrepair.feature.chat

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.AgentResponse
import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import com.aa.carrepair.domain.model.SafetyClassification
import com.aa.carrepair.domain.model.SafetyLevel
import com.aa.carrepair.domain.usecase.chat.GetChatHistoryUseCase
import com.aa.carrepair.domain.usecase.chat.SendMessageUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val sendMessageUseCase: SendMessageUseCase = mockk()
    private val getChatHistoryUseCase: GetChatHistoryUseCase = mockk()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getChatHistoryUseCase(any()) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ChatViewModel(sendMessageUseCase, getChatHistoryUseCase)

    // --- initSession ---

    @Test
    fun `initSession with new generates UUID sessionId`() = runTest {
        viewModel = createViewModel()
        viewModel.initSession("new")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.sessionId.isNotBlank())
        assertTrue(viewModel.uiState.value.sessionId != "new")
    }

    @Test
    fun `initSession with existing id keeps id`() = runTest {
        viewModel = createViewModel()
        viewModel.initSession("session-123")
        advanceUntilIdle()
        assertEquals("session-123", viewModel.uiState.value.sessionId)
    }

    @Test
    fun `initSession observes chat history`() = runTest {
        val messages = listOf(
            ChatMessage("1", "s1", "Hello", MessageRole.USER, timestamp = Instant.parse("2024-01-01T00:00:00Z")),
            ChatMessage("2", "s1", "Hi!", MessageRole.ASSISTANT, timestamp = Instant.parse("2024-01-01T00:00:01Z"))
        )
        every { getChatHistoryUseCase("s1") } returns flowOf(messages)

        viewModel = createViewModel()
        viewModel.initSession("s1")
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.messages.size)
        assertEquals("Hello", viewModel.uiState.value.messages[0].content)
    }

    // --- onInputChanged ---

    @Test
    fun `onInputChanged updates inputText`() {
        viewModel = createViewModel()
        viewModel.onInputChanged("brake problem")
        assertEquals("brake problem", viewModel.uiState.value.inputText)
    }

    // --- sendMessage ---

    @Test
    fun `sendMessage with blank input does nothing`() = runTest {
        viewModel = createViewModel()
        viewModel.onInputChanged("   ")
        viewModel.sendMessage()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isTyping)
    }

    @Test
    fun `sendMessage success updates agent type`() = runTest {
        val response = AgentResponse(
            content = "Check your oil",
            agentType = AgentType.DIAGNOSIS,
            confidence = 85,
            safetyAssessment = null
        )
        coEvery { sendMessageUseCase(any(), any(), any()) } returns DataResult.Success(response)

        viewModel = createViewModel()
        viewModel.initSession("s1")
        viewModel.onInputChanged("my engine light is on")
        viewModel.sendMessage()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isTyping)
        assertEquals(AgentType.DIAGNOSIS, viewModel.uiState.value.currentAgentType)
        assertEquals("", viewModel.uiState.value.inputText)
    }

    @Test
    fun `sendMessage error sets error message`() = runTest {
        coEvery { sendMessageUseCase(any(), any(), any()) } returns
            DataResult.Error(RuntimeException("Network error"))

        viewModel = createViewModel()
        viewModel.initSession("s1")
        viewModel.onInputChanged("help")
        viewModel.sendMessage()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isTyping)
        assertEquals("Failed to send message. Please try again.", viewModel.uiState.value.error)
    }

    @Test
    fun `sendMessage clears input immediately`() = runTest {
        coEvery { sendMessageUseCase(any(), any(), any()) } returns
            DataResult.Success(AgentResponse("ok", AgentType.GENERAL, 90, null))

        viewModel = createViewModel()
        viewModel.initSession("s1")
        viewModel.onInputChanged("test")
        viewModel.sendMessage()

        assertEquals("", viewModel.uiState.value.inputText)
    }

    @Test
    fun `sendMessage passes vehicleVin to use case`() = runTest {
        coEvery { sendMessageUseCase("s1", "oil change", "1HGCM82633A004352") } returns
            DataResult.Success(AgentResponse("Done", AgentType.ESTIMATOR, 70, null))

        viewModel = createViewModel()
        viewModel.initSession("s1")
        viewModel.onInputChanged("oil change")
        viewModel.sendMessage(vehicleVin = "1HGCM82633A004352")
        advanceUntilIdle()

        assertEquals(AgentType.ESTIMATOR, viewModel.uiState.value.currentAgentType)
    }

    // --- clearError ---

    @Test
    fun `clearError resets error to null`() = runTest {
        coEvery { sendMessageUseCase(any(), any(), any()) } returns
            DataResult.Error(RuntimeException("fail"))

        viewModel = createViewModel()
        viewModel.initSession("s1")
        viewModel.onInputChanged("test")
        viewModel.sendMessage()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }
}
