package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: Int): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET failedLoginAttempts = failedLoginAttempts + 1 WHERE email = :email")
    suspend fun incrementFailedAttempts(email: String)

    @Query("UPDATE users SET isLocked = 1 WHERE email = :email AND failedLoginAttempts >= 3")
    suspend fun checkAndLockAccount(email: String)

    @Query("UPDATE users SET failedLoginAttempts = 0, isLocked = 0, lastLoginTimestamp = :timestamp WHERE id = :userId")
    suspend fun recordSuccessfulLogin(userId: Int, timestamp: Long = System.currentTimeMillis())
}
