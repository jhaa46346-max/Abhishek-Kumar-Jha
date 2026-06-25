package com.example.data

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object SecurityUtils {
    private const val SESSION_EXPIRY_MILLIS = 30L * 24 * 60 * 60 * 1000L // 30 Days

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return android.util.Base64.encodeToString(saltBytes, android.util.Base64.NO_WRAP)
    }

    fun hashPassword(password: String, salt: String): String {
        val combined = password + salt
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return android.util.Base64.encodeToString(hashedBytes, android.util.Base64.NO_WRAP)
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computedHash = hashPassword(password, salt)
        return computedHash == expectedHash
    }

    data class PasswordValidationResult(
        val isValid: Boolean,
        val hasMinLength: Boolean,
        val hasUppercase: Boolean,
        val hasLowercase: Boolean,
        val hasDigit: Boolean,
        val hasSpecial: Boolean
    )

    fun validatePasswordComplexity(password: String): PasswordValidationResult {
        val hasMinLength = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        val isValid = hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSpecial
        return PasswordValidationResult(
            isValid = isValid,
            hasMinLength = hasMinLength,
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasDigit = hasDigit,
            hasSpecial = hasSpecial
        )
    }

    fun generate6DigitOtp(): String {
        val random = SecureRandom()
        val number = random.nextInt(900000) + 100000
        return number.toString()
    }

    fun isSessionExpired(lastLoginTimestamp: Long, currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
        if (lastLoginTimestamp == 0L) return true
        return (currentTimeMillis - lastLoginTimestamp) > SESSION_EXPIRY_MILLIS
    }

    fun getDaysRemaining(lastLoginTimestamp: Long): Int {
        if (lastLoginTimestamp == 0L) return 0
        val elapsed = System.currentTimeMillis() - lastLoginTimestamp
        val remainingMillis = SESSION_EXPIRY_MILLIS - elapsed
        if (remainingMillis <= 0) return 0
        return (remainingMillis / (24 * 60 * 60 * 1000L)).toInt()
    }

    fun generateWatermarkHash(email: String, ideaTitle: String, timestamp: Long): String {
        val raw = "$email::$ideaTitle::$timestamp::NEXUS_STUDENT_IP_SHIELD"
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }.take(20).uppercase()
    }
}
