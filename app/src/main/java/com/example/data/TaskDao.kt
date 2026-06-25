package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM study_tasks WHERE userId = :userId ORDER BY isCompleted ASC, dueDate ASC")
    fun getTasksByUser(userId: Int): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity): Long

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Delete
    suspend fun deleteTask(task: StudyTaskEntity)

    @Query("SELECT COUNT(*) FROM study_tasks WHERE userId = :userId AND isCompleted = 1")
    fun getCompletedTaskCountFlow(userId: Int): Flow<Int>
}
