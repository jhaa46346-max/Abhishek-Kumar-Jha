package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        IdeaVaultEntity::class,
        StudyTaskEntity::class,
        SecurityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NexusDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun ideaDao(): IdeaDao
    abstract fun taskDao(): TaskDao
    abstract fun securityLogDao(): SecurityLogDao

    companion object {
        @Volatile
        private var INSTANCE: NexusDatabase? = null

        fun getDatabase(context: Context): NexusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NexusDatabase::class.java,
                    "nexus_student_secure_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
