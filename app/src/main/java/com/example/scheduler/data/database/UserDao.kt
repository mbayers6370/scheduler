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
     * Retrieves a user by their unique username for login validation.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUser(username: String): User?
}
