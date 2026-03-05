package com.aa.carrepair.analytics

import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class TopicDiscoveryTest {

    private lateinit var topicDiscovery: TopicDiscovery

    @Before
    fun setUp() {
        topicDiscovery = TopicDiscovery()
    }

    private fun userMsg(content: String) = ChatMessage(
        id = "m-${content.hashCode()}",
        sessionId = "s1",
        content = content,
        role = MessageRole.USER,
        timestamp = Instant.parse("2024-01-01T00:00:00Z")
    )

    private fun assistantMsg(content: String) = ChatMessage(
        id = "m-${content.hashCode()}",
        sessionId = "s1",
        content = content,
        role = MessageRole.ASSISTANT,
        timestamp = Instant.parse("2024-01-01T00:00:01Z")
    )

    @Test
    fun `discover returns empty for empty messages`() {
        assertEquals(emptyList<String>(), topicDiscovery.discover(emptyList()))
    }

    @Test
    fun `discover filters only user messages`() {
        val messages = listOf(
            assistantMsg("brake pads need replacement immediately"),
            assistantMsg("engine is very important")
        )
        // Only assistant messages, no user messages to discover from
        val topics = topicDiscovery.discover(messages)
        assertTrue(topics.isEmpty())
    }

    @Test
    fun `discover returns topics from user messages`() {
        val messages = listOf(
            userMsg("My transmission is grinding when shifting gears"),
            userMsg("I also hear brake noise")
        )
        val topics = topicDiscovery.discover(messages)
        assertTrue(topics.isNotEmpty())
    }

    @Test
    fun `discover respects topN limit`() {
        val messages = listOf(
            userMsg("transmission brake engine alternator maintenance filter cylinder"),
        )
        val topics = topicDiscovery.discover(messages, topN = 3)
        assertTrue(topics.size <= 3)
    }

    @Test
    fun `getTopTopics delegates to discover with larger N`() {
        val messages = listOf(
            userMsg("brake pads transmission engine battery"),
        )
        val topics = topicDiscovery.getTopTopics(messages)
        assertTrue(topics.size <= 10)
    }

    @Test
    fun `stopwords are filtered out`() {
        val messages = listOf(
            userMsg("the is are was were have has had")
        )
        val topics = topicDiscovery.discover(messages)
        // All stopwords, should return empty (words <= 3 chars also filtered)
        assertTrue(topics.isEmpty())
    }

    @Test
    fun `short words under 4 chars are filtered`() {
        val messages = listOf(
            userMsg("a an by it my we in on at to")
        )
        val topics = topicDiscovery.discover(messages)
        assertTrue(topics.isEmpty())
    }

    @Test
    fun `words with punctuation are cleaned`() {
        val messages = listOf(
            userMsg("What about transmission? And brakes!")
        )
        val topics = topicDiscovery.discover(messages)
        // "transmission" and "brakes" should survive after cleaning
        assertTrue(topics.any { it.contains("transmission") || it.contains("brakes") })
    }
}
