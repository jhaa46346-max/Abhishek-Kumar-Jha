package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeaDao {
    @Query("SELECT * FROM idea_vault WHERE userId = :userId ORDER BY createdAt DESC")
    fun getIdeasByUser(userId: Int): Flow<List<IdeaVaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: IdeaVaultEntity): Long

    @Delete
    suspend fun deleteIdea(idea: IdeaVaultEntity)

    @Query("SELECT COUNT(*) FROM idea_vault WHERE userId = :userId")
    fun getIdeaCountFlow(userId: Int): Flow<Int>
}
