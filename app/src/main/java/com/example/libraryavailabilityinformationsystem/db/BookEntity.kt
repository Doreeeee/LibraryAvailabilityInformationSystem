package com.example.libraryavailabilityinformationsystem.db

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libraryavailabilityinformationsystem.models.Book
import com.example.libraryavailabilityinformationsystem.models.Status

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val author: String,
    val year: Int = 2024,
    val category: String = "General",
    val status: String = "Available",
    val copies: Int = 1,
    val total: Int = 1,
    val location: String = "",
    val shelf: String = "",
    val returnDate: String? = null,
    val isOutdated: Boolean = false,
    val description: String = "Book details and info.",
    val createdBy: String = "Librarian Staff",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val syncStatus: String = "SYNCED"
)

fun BookEntity.toModel(): Book {
    val statusEnum = when (status.uppercase()) {
        "BORROWED" -> Status.Borrowed
        "UNAVAILABLE" -> Status.Unavailable
        else -> Status.Available
    }
    return Book(
        id = id,
        title = title,
        author = author,
        year = year,
        category = category,
        status = statusEnum,
        copies = copies,
        total = total,
        location = location,
        shelf = shelf,
        returnDate = returnDate,
        coverColors = listOf(Color(0xFF0E3768), Color(0xFF081B31)),
        isOutdated = isOutdated,
        description = description,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun Book.toEntity(syncStatus: String = "SYNCED"): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        year = year,
        category = category,
        status = status.name,
        copies = copies,
        total = total,
        location = location,
        shelf = shelf,
        returnDate = returnDate,
        isOutdated = isOutdated,
        description = description,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive,
        syncStatus = syncStatus
    )
}
