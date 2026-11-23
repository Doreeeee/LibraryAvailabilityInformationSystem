package com.example.libraryavailabilityinformationsystem.util

import java.security.MessageDigest

object PasswordHasher {
    fun hashPassword(password: String): String {
        if (password.isEmpty()) return ""
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            password
        }
    }

    fun verifyPassword(inputPassword: String, storedHash: String): Boolean {
        if (storedHash.isEmpty()) return true
        if (inputPassword == storedHash) return true
        val hashedInput = hashPassword(inputPassword)
        return hashedInput == storedHash
    }
}
