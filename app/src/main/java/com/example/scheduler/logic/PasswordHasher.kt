package com.example.scheduler.logic

import android.util.Base64
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Utility for securely hashing and verifying passwords using PBKDF2 with HMAC-SHA256.
 * Satisfies Task IV: Industry-Standard Best Practices for credential security.
 */
object PasswordHasher {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    /**
     * Generates a salted hash for the provided plaintext password.
     * @return A string containing both the salt and the hash, separated by a colon.
     */
    fun hashPassword(password: String): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        
        val hash = deriveHash(password, salt)
        
        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)
        
        return "$saltBase64:$hashBase64"
    }

    /**
     * Verifies a plaintext password against a previously generated salted hash.
     * @param password The plaintext password to verify.
     * @param storedValue The "salt:hash" string stored in the database.
     */
    fun checkPassword(password: String, storedValue: String): Boolean {
        return try {
            val parts = storedValue.split(":")
            if (parts.size != 2) return false
            
            val salt = Base64.decode(parts[0], Base64.NO_WRAP)
            val storedHash = parts[1]
            
            val currentHash = deriveHash(password, salt)
            val currentHashBase64 = Base64.encodeToString(currentHash, Base64.NO_WRAP)
            
            storedHash == currentHashBase64
        } catch (e: Exception) {
            false
        }
    }

    private fun deriveHash(password: String, salt: ByteArray): ByteArray {
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }
}
