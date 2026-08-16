package com.example.scheduler.ui.screens

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a system user with authentication credentials and profile information.
 * This entity is mapped to the 'users' table in the local SQLite database.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String
)
