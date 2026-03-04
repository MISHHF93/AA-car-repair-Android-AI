package com.aa.carrepair.analytics

import com.aa.carrepair.domain.model.ChatMessage
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsEngine @Inject constructor(
    private val conversationSummarizer: ConversationSummarizer,
    private val topicDiscovery: TopicDiscovery,
    private val predictiveInsights: PredictiveInsights
) {
    fun analyzeChatSession(messages: List<ChatMessage>): SessionAnalytics {
        val summary = conversationSummarizer.summarize(messages)
        val topics = topicDiscovery.discover(messages)
        val sentiment = analyzeSentiment(messages)

        Timber.d("Session analytics: topics=%s, sentiment=%.2f", topics, sentiment)

        return SessionAnalytics(
            summary = summary,
            topics = topics,
            sentiment = sentiment,
            messageCount = messages.size,
            agentTypes = messages.map { it.agentType.name }.distinct()
        )
    }

    fun getBusinessMetrics(sessions: List<List<ChatMessage>>): BusinessMetrics {
        val totalSessions = sessions.size
        val avgMessagesPerSession = if (totalSessions > 0) {
            sessions.sumOf { it.size }.toDouble() / totalSessions
        } else 0.0
        val estimateSessionCount = sessions.count { session ->
            session.any { msg -> msg.content.lowercase().contains("estimate") }
        }
        val estimateCompletionRate = if (totalSessions > 0) {
            estimateSessionCount.toDouble() / totalSessions * 100
        } else 0.0

        return BusinessMetrics(
            totalSessions = totalSessions,
            avgMessagesPerSession = avgMessagesPerSession,
            estimateCompletionRate = estimateCompletionRate,
            topTopics = topicDiscovery.getTopTopics(sessions.flatten())
        )
    }

    private fun analyzeSentiment(messages: List<ChatMessage>): Double {
        val userMessages = messages.filter { it.role == com.aa.carrepair.domain.model.MessageRole.USER }
        if (userMessages.isEmpty()) return 0.0

        val positiveWords = setOf("great", "good", "thanks", "helpful", "excellent", "perfect", "works")
        val negativeWords = setOf("bad", "broken", "terrible", "wrong", "error", "fail", "problem")

        val scores = userMessages.map { msg ->
            val lower = msg.content.lowercase()
            val pos = positiveWords.count { lower.contains(it) }
            val neg = negativeWords.count { lower.contains(it) }
            (pos - neg).toDouble()
        }

        return if (scores.isNotEmpty()) scores.average() else 0.0
    }
}

data class SessionAnalytics(
    val summary: String,
    val topics: List<String>,
    val sentiment: Double,
    val messageCount: Int,
    val agentTypes: List<String>
)

data class BusinessMetrics(
    val totalSessions: Int,
    val avgMessagesPerSession: Double,
    val estimateCompletionRate: Double,
    val topTopics: List<String>
)
