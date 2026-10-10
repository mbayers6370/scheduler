package com.example.scheduler.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scheduler.data.model.User

/**
 * Data Access Object for handling user-related database operations.
 * Manages user registration and credential retrieval for the authentication flow.
 */
@Dao
interface UserDao {
    /**
     * Registers a new user account. 
     * Uses [OnConflictStrategy.ABORT] to ensure usernames remain unique.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User)

    /**
     * Retrieves a user by their username or email address for login validation (case-insensitive).
     */
    @Query("SELECT * FROM users WHERE LOWER(TRIM(username)) = LOWER(TRIM(:identifier)) OR LOWER(TRIM(email)) = LOWER(TRIM(:identifier)) LIMIT 1")
    suspend fun getUser(identifier: String): User?
}
