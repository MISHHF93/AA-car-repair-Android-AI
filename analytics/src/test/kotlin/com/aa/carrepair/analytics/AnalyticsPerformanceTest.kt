package com.aa.carrepair.analytics

import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class AnalyticsPerformanceTest {

    private lateinit var summarizer: ConversationSummarizer
    private lateinit var topicDiscovery: TopicDiscovery
    private lateinit var predictiveInsights: PredictiveInsights

    @Before
    fun setUp() {
        summarizer = ConversationSummarizer()
        topicDiscovery = TopicDiscovery()
        predictiveInsights = PredictiveInsights()
    }

    private fun generateMessages(count: Int): List<ChatMessage> =
        (0 until count).map { i ->
            ChatMessage(
                id = "m-$i",
                sessionId = "s1",
                content = "Message about ${if (i % 3 == 0) "brake pads" else if (i % 3 == 1) "engine oil" else "transmission fluid"} issue number $i",
                role = if (i % 2 == 0) MessageRole.USER else MessageRole.ASSISTANT,
                timestamp = Instant.parse("2024-01-01T00:00:00Z").plusSeconds(i.toLong() * 60)
            )
        }

    @Test
    fun `summarize 1000 messages under 200ms`() {
        val messages = generateMessages(1000)
        val start = System.nanoTime()
        val result = summarizer.summarize(messages)
        val elapsed = (System.nanoTime() - start) / 1_000_000
        assertTrue("Summarize took ${elapsed}ms, expected < 200ms", elapsed < 200)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `topic discovery 1000 messages under 500ms`() {
        val messages = generateMessages(1000)
        val start = System.nanoTime()
        val topics = topicDiscovery.discover(messages)
        val elapsed = (System.nanoTime() - start) / 1_000_000
        assertTrue("Topic discovery took ${elapsed}ms, expected < 500ms", elapsed < 500)
        assertTrue(topics.isNotEmpty())
    }

    @Test
    fun `predictMaintenanceCost 10000 predictions under 200ms`() {
        val costs = (1..20).map { it * 50.0 }
        val start = System.nanoTime()
        repeat(10_000) {
            predictiveInsights.predictMaintenanceCost(5, 50000, costs)
        }
        val elapsed = (System.nanoTime() - start) / 1_000_000
        assertTrue("10k predictions took ${elapsed}ms, expected < 200ms", elapsed < 200)
    }

    @Test
    fun `identifyMaintenancePattern 10000 calls under 200ms`() {
        val dayMs = 86400000L
        val dates = (0..10).map { it * 90 * dayMs }
        val mileages = (0..10).map { it * 5000 }
        val start = System.nanoTime()
        repeat(10_000) {
            predictiveInsights.identifyMaintenancePattern(dates, mileages)
        }
        val elapsed = (System.nanoTime() - start) / 1_000_000
        assertTrue("10k pattern analyses took ${elapsed}ms, expected < 200ms", elapsed < 200)
    }

    @Test
    fun `topic discovery with 5000 messages under 2s`() {
        val messages = generateMessages(5000)
        val start = System.nanoTime()
        val topics = topicDiscovery.discover(messages)
        val elapsed = (System.nanoTime() - start) / 1_000_000
        assertTrue("5k message discovery took ${elapsed}ms, expected < 2000ms", elapsed < 2000)
    }
}
