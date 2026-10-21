package com.example.libraryavailabilityinformationsystem

import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.Book
import com.example.libraryavailabilityinformationsystem.models.Status
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BookAvailabilityTest {

    private lateinit var books: MutableList<Book>

    @Before
    fun setUp() {
        books = LaisRepository.initialBooks.toMutableList()
    }

    @Test
    fun testAvailableBookHasPositiveCopyCount() {
        val availableBook = books.first { it.status == Status.Available }
        assertTrue(availableBook.copies > 0)
        assertTrue(availableBook.copies <= availableBook.total)
        assertNull(availableBook.returnDate)
    }

    @Test
    fun testBorrowedBookHasExpectedReturnDate() {
        val borrowedBook = books.first { it.status == Status.Borrowed }
        assertEquals(0, borrowedBook.copies)
        assertNotNull(borrowedBook.returnDate)
        assertFalse(borrowedBook.returnDate.isNullOrBlank())
    }

    @Test
    fun testUnavailableBookHasZeroCopies() {
        val unavailableBook = books.first { it.status == Status.Unavailable }
        assertEquals(0, unavailableBook.copies)
        assertEquals(0, unavailableBook.total)
    }

    @Test
    fun testLibrarianMarksBookAsReturned() {
        val borrowedIndex = books.indexOfFirst { it.status == Status.Borrowed }
        assertTrue(borrowedIndex != -1)

        val targetBook = books[borrowedIndex]
        val updatedBook = targetBook.copy(
            status = Status.Available,
            copies = (targetBook.copies + 1).coerceAtMost(targetBook.total),
            returnDate = null
        )
        books[borrowedIndex] = updatedBook

        val result = books[borrowedIndex]
        assertEquals(Status.Available, result.status)
        assertTrue(result.copies > 0)
        assertNull(result.returnDate)
    }

    @Test
    fun testLibrarianUpdatesReturnDate() {
        val borrowedIndex = books.indexOfFirst { it.status == Status.Borrowed }
        val targetBook = books[borrowedIndex]
        val newReturnDate = "30 June 2025"

        val updatedBook = targetBook.copy(returnDate = newReturnDate, status = Status.Borrowed)
        books[borrowedIndex] = updatedBook

        assertEquals(newReturnDate, books[borrowedIndex].returnDate)
        assertEquals(Status.Borrowed, books[borrowedIndex].status)
    }
}
