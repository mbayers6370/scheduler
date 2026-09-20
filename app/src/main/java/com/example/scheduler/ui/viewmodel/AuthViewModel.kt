package com.example.scheduler.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduler.data.database.AppDatabase
import com.example.scheduler.data.model.User
import com.example.scheduler.data.repository.UserRepository
import com.example.scheduler.domain.usecase.AuthenticateUserUseCase
import com.example.scheduler.logic.PasswordHasher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for user authentication and session management.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    private val authenticateUserUseCase: AuthenticateUserUseCase

    /**
     * Observable state for the currently authenticated user.
     */
    val currentUser = MutableStateFlow<User?>(null)

    init {
        val db = AppDatabase.getDatabase(application)
        userRepository = UserRepository(db.userDao())
        authenticateUserUseCase = AuthenticateUserUseCase(userRepository)
    }

    /**
     * Validates credentials against the database.
     */
    suspend fun loginUser(username: String, password: String): Boolean {
        val user = authenticateUserUseCase(username, password)
        return if (user != null) {
            currentUser.value = user
            true
        } else {
            false
        }
    }

    /**
     * Registers a new user account with hashed password.
     */
    suspend fun registerUser(
        username: String, 
        password: String,
        firstName: String,
        lastName: String,
        email: String
    ): Boolean {
        return try {
            val hashedPassword = PasswordHasher.hashPassword(password)
            val newUser = User(username, hashedPassword, firstName, lastName, email)
            userRepository.registerUser(newUser)
            currentUser.value = newUser
            true
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        currentUser.value = null
    }
}
