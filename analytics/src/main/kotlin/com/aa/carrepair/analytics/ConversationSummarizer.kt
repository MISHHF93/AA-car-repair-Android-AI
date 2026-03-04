package com.aa.carrepair.analytics

import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationSummarizer @Inject constructor() {

    fun summarize(messages: List<ChatMessage>, maxLength: Int = 300): String {
        if (messages.isEmpty()) return ""

        val userMessages = messages.filter { it.role == MessageRole.USER }
        val assistantMessages = messages.filter { it.role == MessageRole.ASSISTANT }

        val keyTopics = extractKeyTopics(userMessages)
        val questionCount = userMessages.count { it.content.endsWith("?") }
        val sessionDuration = if (messages.size >= 2) {
            val durationMs = messages.last().timestamp.toEpochMilli() - messages.first().timestamp.toEpochMilli()
            "${durationMs / 60000} minutes"
        } else "< 1 minute"

        return buildString {
            append("Session: ${messages.size} messages over $sessionDuration. ")
            append("User asked $questionCount questions. ")
            if (keyTopics.isNotEmpty()) {
                append("Topics: ${keyTopics.joinToString(", ")}. ")
            }
            if (assistantMessages.isNotEmpty()) {
                val lastAssistant = assistantMessages.last().content
                append("Last response: ${lastAssistant.take(100)}")
                if (lastAssistant.length > 100) append("...")
            }
        }.take(maxLength)
    }

    private fun extractKeyTopics(messages: List<ChatMessage>): List<String> {
        val allText = messages.joinToString(" ") { it.content.lowercase() }
        val topicKeywords = mapOf(
            "brakes" to listOf("brake", "braking", "rotor", "caliper", "pad"),
            "engine" to listOf("engine", "motor", "cylinder", "piston", "timing"),
            "transmission" to listOf("transmission", "gear", "shift", "clutch"),
            "electrical" to listOf("battery", "alternator", "electrical", "fuse", "wiring"),
            "dtc" to listOf("code", "p0", "b0", "c0", "u0", "fault"),
            "estimate" to listOf("cost", "price", "estimate", "quote", "how much"),
            "maintenance" to listOf("oil change", "service", "maintenance", "tune-up", "filter")
        )
        return topicKeywords.filter { (_, keywords) ->
            keywords.any { allText.contains(it) }
        }.keys.toList()
    }
}
