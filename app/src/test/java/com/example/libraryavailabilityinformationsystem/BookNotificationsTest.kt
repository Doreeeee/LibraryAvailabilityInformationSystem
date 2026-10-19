package com.example.libraryavailabilityinformationsystem

import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.Book
import com.example.libraryavailabilityinformationsystem.models.Status
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BookNotificationsTest {

    private lateinit var books: List<Book>
    private lateinit var alertIds: MutableList<Int>
    private lateinit var savedIds: MutableList<Int>

    @Before
    fun setUp() {
        books = LaisRepository.initialBooks
        alertIds = mutableListOf()
        savedIds = mutableListOf()
    }

    private fun toggleInList(list: MutableList<Int>, id: Int) {
        if (!list.remove(id)) {
            list.add(id)
        }
    }

    @Test
    fun testToggleSavedBook_addsAndRemovesId() {
        val targetBookId = 1

        toggleInList(savedIds, targetBookId)
        assertTrue(savedIds.contains(targetBookId))
        assertEquals(1, savedIds.size)

        toggleInList(savedIds, targetBookId)
        assertFalse(savedIds.contains(targetBookId))
        assertTrue(savedIds.isEmpty())
    }

    @Test
    fun testToggleNotificationAlert_forUnavailableBook() {
        val unavailableBook = books.first { it.status == Status.Unavailable }

        toggleInList(alertIds, unavailableBook.id)
        assertTrue(alertIds.contains(unavailableBook.id))

        toggleInList(alertIds, unavailableBook.id)
        assertFalse(alertIds.contains(unavailableBook.id))
    }

    @Test
    fun testToggleNotificationAlert_forBorrowedBook() {
        val borrowedBook = books.first { it.status == Status.Borrowed }

        toggleInList(alertIds, borrowedBook.id)
        assertTrue(alertIds.contains(borrowedBook.id))
    }

    @Test
    fun testFilterNotificationBooks_matchesAlertIds() {
        val borrowedBook = books.first { it.status == Status.Borrowed }
        val unavailableBook = books.first { it.status == Status.Unavailable }

        toggleInList(alertIds, borrowedBook.id)
        toggleInList(alertIds, unavailableBook.id)

        val alertedBooks = books.filter { it.id in alertIds }
        assertEquals(2, alertedBooks.size)
        assertTrue(alertedBooks.contains(borrowedBook))
        assertTrue(alertedBooks.contains(unavailableBook))
    }
}
