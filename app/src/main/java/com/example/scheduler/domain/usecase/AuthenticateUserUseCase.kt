package com.example.scheduler.domain.usecase

import com.example.scheduler.data.model.User
import com.example.scheduler.data.repository.UserRepository
import com.example.scheduler.logic.PasswordHasher

/**
 * Use Case for validating user credentials.
 */
class AuthenticateUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(username: String, password: String): User? {
        val user = userRepository.getUser(username)
        return if (user != null && PasswordHasher.checkPassword(password, user.password)) {
            user
        } else {
            null
        }
    }
}
