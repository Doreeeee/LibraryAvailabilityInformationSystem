package com.example.libraryavailabilityinformationsystem.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libraryavailabilityinformationsystem.models.BorrowHistoryItem

@Entity(tableName = "borrow_history")
data class BorrowHistoryEntity(
    @PrimaryKey
    val id: Int,
    val bookId: Int,
    val bookTitle: String,
    val author: String,
    val borrowDate: String,
    val returnDate: String,
    val status: String
)

fun BorrowHistoryEntity.toModel(): BorrowHistoryItem {
    return BorrowHistoryItem(
        id = id,
        bookId = bookId,
        bookTitle = bookTitle,
        author = author,
        borrowDate = borrowDate,
        returnDate = returnDate,
        status = status
    )
}

fun BorrowHistoryItem.toEntity(): BorrowHistoryEntity {
    return BorrowHistoryEntity(
        id = id,
        bookId = bookId,
        bookTitle = bookTitle,
        author = author,
        borrowDate = borrowDate,
        returnDate = returnDate,
        status = status
    )
}
