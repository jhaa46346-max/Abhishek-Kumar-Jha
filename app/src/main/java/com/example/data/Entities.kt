package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val studentId: String,
    val universityName: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginTimestamp: Long = System.currentTimeMillis(),
    val failedLoginAttempts: Int = 0,
    val isLocked: Boolean = false,
    val securityQuestion: String = "What was your first school name?",
    val securityAnswerHash: String = ""
)

@Entity(tableName = "idea_vault")
data class IdeaVaultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val category: String, // SaaS, AI/ML, Cybersecurity, EdTech, FinTech
    val problemStatement: String,
    val proposedSolution: String,
    val targetAudience: String,
    val watermarkFingerprint: String,
    val patentScore: Int,
    val isEncrypted: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val subject: String,
    val dueDate: String,
    val priority: String, // High, Medium, Low
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "security_logs")
data class SecurityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val actionType: String, // LOGIN_SUCCESS, LOGIN_FAILED, OTP_VERIFIED, IDEA_LOCKED, SESSION_EXPIRED
    val details: String,
    val ipAddress: String = "127.0.0.1 (Student Sandbox)",
    val timestamp: Long = System.currentTimeMillis()
)
