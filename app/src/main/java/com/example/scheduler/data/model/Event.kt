package com.example.scheduler.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a scheduled item, which can be a single event or a collection (folder) of events.
 * Stored in the 'events' table with support for hierarchical nesting via [parentCollectionId].
 * Belongs to a specific [User] identified by [userId].
 */
@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["parentCollectionId"]),
        Index(value = ["userId"])
    ]
)
data class Event(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val date: String,
    val time: String,
    val iconName: String,
    val location: String? = null,
    val notes: String? = null,
    val reminder: String? = null,
    val isCollection: Boolean = false,
    val dateRange: String? = null,
    val eventCount: Int? = null,
    val timestamp: Long? = null,
    val durationMinutes: Int = 60,
    val parentCollectionId: String? = null
)
