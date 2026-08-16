package com.example.scheduler.ui.screens

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a scheduled item, which can be a single event or a collection (folder) of events.
 * Stored in the 'events' table with support for hierarchical nesting via [parentCollectionId].
 */
@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
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
    val parentCollectionId: String? = null
)
