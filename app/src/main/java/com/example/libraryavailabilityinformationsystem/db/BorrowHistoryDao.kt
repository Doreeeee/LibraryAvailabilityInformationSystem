package com.example.libraryavailabilityinformationsystem.db

import androidx.room.*

@Dao
interface BorrowHistoryDao {
    @Query("SELECT * FROM borrow_history ORDER BY id DESC")
    fun getAllHistory(): List<BorrowHistoryEntity>

    @Query("SELECT * FROM borrow_history WHERE bookId = :bookId ORDER BY id DESC")
    fun getHistoryForBook(bookId: Int): List<BorrowHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(items: List<BorrowHistoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryItem(item: BorrowHistoryEntity)
}
