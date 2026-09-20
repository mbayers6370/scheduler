package com.example.scheduler.data.repository

import com.example.scheduler.data.database.UserDao
import com.example.scheduler.data.model.User

/**
 * Repository class that abstracts access to user data.
 * Handles user registration and retrieval for authentication.
 */
class UserRepository(private val userDao: UserDao) {

    /**
     * Registers a new user.
     */
    suspend fun registerUser(user: User) = userDao.registerUser(user)

    /**
     * Retrieves a user by username.
     */
    suspend fun getUser(username: String): User? = userDao.getUser(username)
}
