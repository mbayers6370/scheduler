package com.example.scheduler.data.repository

import com.example.scheduler.data.database.EventDao
import com.example.scheduler.data.model.Event
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to event data.
 * Provides a clean API for the rest of the application to interact with Event entities.
 */
class EventRepository(private val eventDao: EventDao) {

    /**
     * Returns a flow of all events in the database belonging to a specific user.
     */
    fun getAllEvents(userId: String): Flow<List<Event>> = eventDao.getAllEvents(userId)

    /**
     * Inserts or updates an event.
     */
    suspend fun insertEvent(event: Event) = eventDao.insertEvent(event)

    /**
     * Updates an existing event.
     */
    suspend fun updateEvent(event: Event) = eventDao.updateEvent(event)

    /**
     * Deletes an event.
     */
    suspend fun deleteEvent(event: Event) = eventDao.deleteEvent(event)
}
