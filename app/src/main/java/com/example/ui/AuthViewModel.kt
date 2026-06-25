package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data class Unauthenticated(val errorMessage: String? = null, val successMessage: String? = null) : AuthUiState
    data class OtpVerificationRequired(
        val userId: Int,
        val email: String,
        val generatedOtp: String,
        val isExpiryReauth: Boolean
    ) : AuthUiState
    data class Authenticated(val user: UserEntity, val daysRemaining: Int) : AuthUiState
    data class AccountLocked(val email: String) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val db = NexusDatabase.getDatabase(application)
    private val userDao = db.userDao()
    private val securityLogDao = db.securityLogDao()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode.asStateFlow()

    init {
        checkCurrentSession()
    }

    fun toggleAuthMode() {
        _isSignUpMode.value = !_isSignUpMode.value
        _uiState.value = AuthUiState.Unauthenticated()
    }

    fun checkCurrentSession() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                val user = userDao.getUserById(userId)
                if (user != null) {
                    if (SecurityUtils.isSessionExpired(user.lastLoginTimestamp)) {
                        logSecurityEvent(user.id, "SESSION_EXPIRED", "30-Day mandatory security re-authentication triggered.")
                        // Trigger OTP re-auth
                        val otp = SecurityUtils.generate6DigitOtp()
                        _uiState.value = AuthUiState.OtpVerificationRequired(user.id, user.email, otp, isExpiryReauth = true)
                    } else {
                        val daysRemaining = SecurityUtils.getDaysRemaining(user.lastLoginTimestamp)
                        _uiState.value = AuthUiState.Authenticated(user, daysRemaining)
                    }
                } else {
                    sessionManager.clearSession()
                    _uiState.value = AuthUiState.Unauthenticated()
                }
            } else {
                _uiState.value = AuthUiState.Unauthenticated()
            }
        }
    }

    fun signUp(fullName: String, studentId: String, university: String, email: String, pass: String, confirmPass: String, secAnswer: String) {
        viewModelScope.launch {
            if (fullName.isBlank() || studentId.isBlank() || university.isBlank() || email.isBlank() || pass.isBlank() || secAnswer.isBlank()) {
                _uiState.value = AuthUiState.Unauthenticated(errorMessage = "All student security fields are required.")
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.value = AuthUiState.Unauthenticated(errorMessage = "Please enter a valid academic/personal email.")
                return@launch
            }
            if (pass != confirmPass) {
                _uiState.value = AuthUiState.Unauthenticated(errorMessage = "Passwords do not match.")
                return@launch
            }

            val complexity = SecurityUtils.validatePasswordComplexity(pass)
            if (!complexity.isValid) {
                _uiState.value = AuthUiState.Unauthenticated(
                    errorMessage = "Password must be >= 8 chars and contain uppercase, lowercase, number, and special symbol (!@#\$%^&*)."
                )
                return@launch
            }

            val existing = userDao.getUserByEmail(email.trim().lowercase())
            if (existing != null) {
                _uiState.value = AuthUiState.Unauthenticated(errorMessage = "Student email is already registered in Nexus Vault.")
                return@launch
            }

            val salt = SecurityUtils.generateSalt()
            val passHash = SecurityUtils.hashPassword(pass, salt)
            val answerHash = SecurityUtils.hashPassword(secAnswer.trim().lowercase(), salt)

            val newUser = UserEntity(
                fullName = fullName.trim(),
                studentId = studentId.trim(),
                universityName = university.trim(),
                email = email.trim().lowercase(),
                passwordHash = passHash,
                salt = salt,
                securityAnswerHash = answerHash
            )

            val insertedId = userDao.insertUser(newUser)
            logSecurityEvent(insertedId.toInt(), "SIGNUP_SUCCESS", "Student shield account created for ${university.trim()}")
            
            // Switch to login with success message
            _isSignUpMode.value = false
            _uiState.value = AuthUiState.Unauthenticated(successMessage = "Account provisioned successfully! Please sign in.")
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            if (email.isBlank() || pass.isBlank()) {
                _uiState.value = AuthUiState.Unauthenticated(errorMessage = "Email and password required.")
                return@launch
            }

            val user = userDao.getUserByEmail(email.trim().lowercase())
            if (user == null) {
                _uiState.value = AuthUiState.Unauthenticated(errorMessage = "Invalid credentials or student account not found.")
                return@launch
            }

            if (user.isLocked) {
                logSecurityEvent(user.id, "LOGIN_BLOCKED", "Attempt on locked account.")
                _uiState.value = AuthUiState.AccountLocked(user.email)
                return@launch
            }

            val isValid = SecurityUtils.verifyPassword(pass, user.salt, user.passwordHash)
            if (!isValid) {
                userDao.incrementFailedAttempts(user.email)
                userDao.checkAndLockAccount(user.email)
                val updatedUser = userDao.getUserByEmail(user.email)
                if (updatedUser?.isLocked == true) {
                    logSecurityEvent(user.id, "ACCOUNT_LOCKED", "Locked after 3 consecutive failed login attempts.")
                    _uiState.value = AuthUiState.AccountLocked(user.email)
                } else {
                    val attemptsLeft = 3 - (updatedUser?.failedLoginAttempts ?: 0)
                    logSecurityEvent(user.id, "LOGIN_FAILED", "Failed attempt. $attemptsLeft attempts remaining.")
                    _uiState.value = AuthUiState.Unauthenticated(
                        errorMessage = "Invalid password. Warning: $attemptsLeft attempts remaining before IP Shield lockout."
                    )
                }
                return@launch
            }

            // Password correct! Generate 2FA OTP
            val otp = SecurityUtils.generate6DigitOtp()
            logSecurityEvent(user.id, "OTP_DISPATCHED", "Simulated secure 2FA token generated: $otp")
            _uiState.value = AuthUiState.OtpVerificationRequired(user.id, user.email, otp, isExpiryReauth = false)
        }
    }

    fun verifyOtp(userId: Int, enteredOtp: String, expectedOtp: String) {
        viewModelScope.launch {
            if (enteredOtp.trim() == expectedOtp.trim()) {
                val now = System.currentTimeMillis()
                userDao.recordSuccessfulLogin(userId, now)
                sessionManager.saveUserId(userId)
                val user = userDao.getUserById(userId)
                if (user != null) {
                    logSecurityEvent(userId, "LOGIN_SUCCESS", "Student verified via 2FA OTP. Session valid for 30 days.")
                    val days = SecurityUtils.getDaysRemaining(now)
                    _uiState.value = AuthUiState.Authenticated(user, days)
                }
            } else {
                val currentState = _uiState.value
                if (currentState is AuthUiState.OtpVerificationRequired) {
                    // Stay on OTP screen but notify
                    logSecurityEvent(userId, "OTP_FAILED", "Invalid OTP entered.")
                    _uiState.value = AuthUiState.Unauthenticated(errorMessage = "Invalid 6-digit 2FA token. Please sign in again.")
                }
            }
        }
    }

    fun unlockAccountWithRecovery(email: String, securityAnswer: String, newPass: String) {
        viewModelScope.launch {
            val user = userDao.getUserByEmail(email.trim().lowercase())
            if (user == null) {
                _uiState.value = AuthUiState.Unauthenticated(errorMessage = "Account not found.")
                return@launch
            }

            val isAnswerValid = SecurityUtils.verifyPassword(securityAnswer.trim().lowercase(), user.salt, user.securityAnswerHash)
            if (!isAnswerValid) {
                _uiState.value = AuthUiState.AccountLocked(email)
                return@launch
            }

            val complexity = SecurityUtils.validatePasswordComplexity(newPass)
            if (!complexity.isValid) {
                _uiState.value = AuthUiState.Unauthenticated(errorMessage = "New password does not meet complexity standards.")
                return@launch
            }

            val newHash = SecurityUtils.hashPassword(newPass, user.salt)
            val unlockedUser = user.copy(
                passwordHash = newHash,
                failedLoginAttempts = 0,
                isLocked = false,
                lastLoginTimestamp = System.currentTimeMillis()
            )
            userDao.updateUser(unlockedUser)
            logSecurityEvent(user.id, "ACCOUNT_UNLOCKED", "Account restored via security answer verification.")
            _uiState.value = AuthUiState.Unauthenticated(successMessage = "Account unlocked & password reset! Please sign in.")
        }
    }

    fun signOut() {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                logSecurityEvent(userId, "LOGOUT", "Student signed out.")
            }
            sessionManager.clearSession()
            _uiState.value = AuthUiState.Unauthenticated(successMessage = "Signed out safely.")
        }
    }

    private suspend fun logSecurityEvent(userId: Int, action: String, details: String) {
        securityLogDao.insertLog(
            SecurityLogEntity(
                userId = userId,
                actionType = action,
                details = details
            )
        )
    }
}
