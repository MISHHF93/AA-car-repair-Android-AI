package com.aa.carrepair.analytics

import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ConversationSummarizerTest {

    private lateinit var summarizer: ConversationSummarizer

    @Before
    fun setUp() {
        summarizer = ConversationSummarizer()
    }

    private fun msg(
        content: String,
        role: MessageRole = MessageRole.USER,
        timestamp: Instant = Instant.parse("2024-01-01T00:00:00Z")
    ) = ChatMessage(
        id = "m-${content.hashCode()}",
        sessionId = "s1",
        content = content,
        role = role,
        timestamp = timestamp
    )

    @Test
    fun `empty messages returns empty string`() {
        assertEquals("", summarizer.summarize(emptyList()))
    }

    @Test
    fun `summarize includes message count`() {
        val messages = listOf(
            msg("Hello", timestamp = Instant.parse("2024-01-01T00:00:00Z")),
            msg("Hi!", MessageRole.ASSISTANT, Instant.parse("2024-01-01T00:05:00Z"))
        )
        val summary = summarizer.summarize(messages)
        assertTrue(summary.contains("2 messages"))
    }

    @Test
    fun `summarize includes question count`() {
        val messages = listOf(
            msg("How much does a brake job cost?"),
            msg("What about rotors?"),
            msg("Let me calculate that", MessageRole.ASSISTANT)
        )
        val summary = summarizer.summarize(messages)
        assertTrue(summary.contains("2 questions"))
    }

    @Test
    fun `summarize extracts brake topic`() {
        val messages = listOf(
            msg("My brake pads are worn"),
            msg("You should replace them", MessageRole.ASSISTANT)
        )
        val summary = summarizer.summarize(messages)
        assertTrue(summary.contains("brakes"))
    }

    @Test
    fun `summarize extracts engine topic`() {
        val messages = listOf(
            msg("My engine is making noise"),
            msg("Could be the timing belt", MessageRole.ASSISTANT)
        )
        val summary = summarizer.summarize(messages)
        assertTrue(summary.contains("engine"))
    }

    @Test
    fun `summarize extracts estimate topic`() {
        val messages = listOf(
            msg("How much does this cost?"),
            msg("The estimate is $500", MessageRole.ASSISTANT)
        )
        val summary = summarizer.summarize(messages)
        assertTrue(summary.contains("estimate"))
    }

    @Test
    fun `summarize respects maxLength`() {
        val messages = listOf(
            msg("This is a long question about brake pads and engine problems and transmission issues?"),
            msg("A".repeat(500), MessageRole.ASSISTANT)
        )
        val summary = summarizer.summarize(messages, maxLength = 100)
        assertTrue(summary.length <= 100)
    }

    @Test
    fun `summarize includes last assistant response truncated`() {
        val messages = listOf(
            msg("Help"),
            msg("Here is the long answer: " + "x".repeat(200), MessageRole.ASSISTANT)
        )
        val summary = summarizer.summarize(messages)
        assertTrue(summary.contains("Last response:"))
    }

    @Test
    fun `summarize calculates session duration`() {
        val messages = listOf(
            msg("Hello", timestamp = Instant.parse("2024-01-01T00:00:00Z")),
            msg("Hi!", MessageRole.ASSISTANT, Instant.parse("2024-01-01T00:10:00Z"))
        )
        val summary = summarizer.summarize(messages)
        assertTrue(summary.contains("10 minutes"))
    }
}
