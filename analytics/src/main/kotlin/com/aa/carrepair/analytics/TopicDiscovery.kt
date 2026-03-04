package com.aa.carrepair.analytics

import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log2

@Singleton
class TopicDiscovery @Inject constructor() {

    private val stopWords = setOf(
        "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
        "have", "has", "had", "do", "does", "did", "will", "would", "could", "should",
        "i", "my", "you", "your", "it", "its", "we", "our", "they", "their",
        "this", "that", "these", "those", "and", "or", "but", "in", "on", "at",
        "to", "for", "of", "with", "by", "from", "how", "what", "when", "where", "why"
    )

    fun discover(messages: List<ChatMessage>, topN: Int = 5): List<String> {
        val userMessages = messages.filter { it.role == MessageRole.USER }
        return computeTfIdf(userMessages).take(topN)
    }

    fun getTopTopics(messages: List<ChatMessage>, topN: Int = 10): List<String> =
        discover(messages, topN)

    private fun computeTfIdf(messages: List<ChatMessage>): List<String> {
        if (messages.isEmpty()) return emptyList()

        val documents = messages.map { tokenize(it.content) }
        val vocabulary = documents.flatten().toSet()
        val wordScores = mutableMapOf<String, Double>()

        vocabulary.forEach { word ->
            val tf = documents.sumOf { doc -> doc.count { it == word } }.toDouble() / documents.sumOf { it.size }
            val docsWithWord = documents.count { doc -> word in doc }
            val idf = if (docsWithWord > 0) log2(documents.size.toDouble() / docsWithWord) else 0.0
            wordScores[word] = tf * idf
        }

        return wordScores.entries
            .sortedByDescending { it.value }
            .map { it.key }
    }

    private fun tokenize(text: String): List<String> =
        text.lowercase()
            .replace(Regex("[^a-z0-9\\s]"), "")
            .split("\\s+".toRegex())
            .filter { it.length > 3 && it !in stopWords }
}
