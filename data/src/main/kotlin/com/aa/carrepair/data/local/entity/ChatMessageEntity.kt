package com.aa.carrepair.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "chat_messages", indices = [Index("session_id")])
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "session_id") val sessionId: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "agent_type") val agentType: String,
    @ColumnInfo(name = "timestamp") val timestamp: Instant,
    @ColumnInfo(name = "confidence") val confidence: Int?,
    @ColumnInfo(name = "safety_level") val safetyLevel: String?,
    @ColumnInfo(name = "attachment_uri") val attachmentUri: String?
)
