package com.example.libraryavailabilityinformationsystem.db

import androidx.room.*

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE isActive = 1 ORDER BY title ASC")
    fun getAllBooks(): List<BookEntity>

    @Query("SELECT * FROM books WHERE isActive = 1 AND (LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(author) LIKE '%' || LOWER(:query) || '%') ORDER BY title ASC")
    fun searchBooks(query: String): List<BookEntity>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getBookById(id: Int): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: BookEntity)

    @Query("UPDATE books SET status = :status, copies = :copies, returnDate = :returnDate, updatedAt = :updatedAt WHERE id = :bookId")
    fun updateBookAvailability(bookId: Int, status: String, copies: Int, returnDate: String?, updatedAt: Long)

    @Query("DELETE FROM books WHERE id = :bookId")
    fun deleteBook(bookId: Int)
}
