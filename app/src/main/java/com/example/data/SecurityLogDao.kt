package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityLogDao {
    @Query("SELECT * FROM security_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    fun getLogsByUser(userId: Int): Flow<List<SecurityLogEntity>>

    @Insert
    suspend fun insertLog(log: SecurityLogEntity)
}
