package com.example.libraryavailabilityinformationsystem.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class, UserEntity::class, BorrowHistoryEntity::class, PendingSyncEntity::class],
    version = 3,
    exportSchema = false
)
abstract class LaisDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao
    abstract fun borrowHistoryDao(): BorrowHistoryDao
    abstract fun pendingSyncDao(): PendingSyncDao

    companion object {
        @Volatile
        private var INSTANCE: LaisDatabase? = null

        fun getInstance(context: Context): LaisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LaisDatabase::class.java,
                    "lais_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
