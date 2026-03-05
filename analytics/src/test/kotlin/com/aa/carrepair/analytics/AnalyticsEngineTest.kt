package com.aa.carrepair.analytics

import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class AnalyticsEngineTest {

    private val summarizer: ConversationSummarizer = mockk()
    private val topicDiscovery: TopicDiscovery = mockk()
    private val predictiveInsights: PredictiveInsights = mockk()

    private lateinit var engine: AnalyticsEngine

    @Before
    fun setUp() {
        engine = AnalyticsEngine(summarizer, topicDiscovery, predictiveInsights)
    }

    private fun msg(
        content: String,
        role: MessageRole = MessageRole.USER,
        agentType: AgentType = AgentType.GENERAL
    ) = ChatMessage(
        id = "m-${content.hashCode()}",
        sessionId = "s1",
        content = content,
        role = role,
        agentType = agentType,
        timestamp = Instant.parse("2024-01-01T00:00:00Z")
    )

    // --- analyzeChatSession ---

    @Test
    fun `analyzeChatSession returns correct message count`() {
        val messages = listOf(
            msg("Hello"),
            msg("Hi there!", MessageRole.ASSISTANT)
        )
        every { summarizer.summarize(messages) } returns "Summary"
        every { topicDiscovery.discover(messages) } returns listOf("greeting")

        val result = engine.analyzeChatSession(messages)
        assertEquals(2, result.messageCount)
    }

    @Test
    fun `analyzeChatSession extracts distinct agent types`() {
        val messages = listOf(
            msg("Check brakes", agentType = AgentType.DIAGNOSIS),
            msg("Result", MessageRole.ASSISTANT, AgentType.DIAGNOSIS),
            msg("Get estimate", agentType = AgentType.ESTIMATOR)
        )
        every { summarizer.summarize(messages) } returns "Summary"
        every { topicDiscovery.discover(messages) } returns emptyList()

        val result = engine.analyzeChatSession(messages)
        assertEquals(listOf("DIAGNOSIS", "ESTIMATOR"), result.agentTypes.sorted())
    }

    @Test
    fun `analyzeChatSession positive sentiment with positive words`() {
        val messages = listOf(
            msg("That was great and helpful!")
        )
        every { summarizer.summarize(messages) } returns "Summary"
        every { topicDiscovery.discover(messages) } returns emptyList()

        val result = engine.analyzeChatSession(messages)
        assertTrue(result.sentiment > 0)
    }

    @Test
    fun `analyzeChatSession negative sentiment with negative words`() {
        val messages = listOf(
            msg("This is terrible, everything is broken and wrong")
        )
        every { summarizer.summarize(messages) } returns "Summary"
        every { topicDiscovery.discover(messages) } returns emptyList()

        val result = engine.analyzeChatSession(messages)
        assertTrue(result.sentiment < 0)
    }

    @Test
    fun `analyzeChatSession neutral sentiment with no sentiment words`() {
        val messages = listOf(
            msg("I need to check my car")
        )
        every { summarizer.summarize(messages) } returns "Summary"
        every { topicDiscovery.discover(messages) } returns emptyList()

        val result = engine.analyzeChatSession(messages)
        assertEquals(0.0, result.sentiment, 0.01)
    }

    @Test
    fun `analyzeChatSession ignores assistant messages for sentiment`() {
        val messages = listOf(
            msg("terrible broken wrong", MessageRole.ASSISTANT)
        )
        every { summarizer.summarize(messages) } returns "Summary"
        every { topicDiscovery.discover(messages) } returns emptyList()

        val result = engine.analyzeChatSession(messages)
        assertEquals(0.0, result.sentiment, 0.01)
    }

    // --- getBusinessMetrics ---

    @Test
    fun `getBusinessMetrics calculates avg messages per session`() {
        val sessions = listOf(
            listOf(msg("a"), msg("b")),
            listOf(msg("c"), msg("d"), msg("e"), msg("f"))
        )
        every { topicDiscovery.getTopTopics(any()) } returns emptyList()

        val metrics = engine.getBusinessMetrics(sessions)
        assertEquals(2, metrics.totalSessions)
        assertEquals(3.0, metrics.avgMessagesPerSession, 0.01)
    }

    @Test
    fun `getBusinessMetrics calculates estimate completion rate`() {
        val sessions = listOf(
            listOf(msg("I need an estimate for brakes")),
            listOf(msg("check my engine")),
            listOf(msg("estimate cost for transmission"))
        )
        every { topicDiscovery.getTopTopics(any()) } returns emptyList()

        val metrics = engine.getBusinessMetrics(sessions)
        assertEquals(66.66, metrics.estimateCompletionRate, 0.5)
    }

    @Test
    fun `getBusinessMetrics with empty sessions`() {
        every { topicDiscovery.getTopTopics(any()) } returns emptyList()

        val metrics = engine.getBusinessMetrics(emptyList())
        assertEquals(0, metrics.totalSessions)
        assertEquals(0.0, metrics.avgMessagesPerSession, 0.01)
        assertEquals(0.0, metrics.estimateCompletionRate, 0.01)
    }
}
