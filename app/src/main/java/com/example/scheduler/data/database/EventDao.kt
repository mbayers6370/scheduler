package com.example.scheduler.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.scheduler.data.model.Event
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing event and collection persistence.
 * Supports full CRUD operations to enable dynamic schedule management.
 */
@Dao
interface EventDao {
    /**
     * Observes all items in the events table belonging to a specific user.
     * Returns a [Flow] to provide real-time updates to the UI as data changes.
     */
    @Query("SELECT * FROM events WHERE userId = :userId")
    fun getAllEvents(userId: String): Flow<List<Event>>

    /**
     * Inserts a new event or updates an existing record.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    /**
     * Updates an existing event's properties.
     */
    @Update
    suspend fun updateEvent(event: Event)

    /**
     * Removes an event or collection from the database.
     */
    @Delete
    suspend fun deleteEvent(event: Event)
}
