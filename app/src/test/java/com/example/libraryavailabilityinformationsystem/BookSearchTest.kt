package com.example.libraryavailabilityinformationsystem

import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.Book
import com.example.libraryavailabilityinformationsystem.models.Status
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BookSearchTest {

    private lateinit var books: List<Book>

    @Before
    fun setUp() {
        books = LaisRepository.initialBooks
    }

    private fun filterBooks(query: String, category: String = "All", availability: String = "All"): List<Book> {
        val q = query.trim().lowercase()
        return books.filter { b ->
            (q.isEmpty() || b.title.lowercase().contains(q) || b.author.lowercase().contains(q)) &&
            (category == "All" || b.category == category) &&
            (availability == "All" || b.status.label == availability)
        }
    }

    @Test
    fun testSearchByTitle_exactMatch() {
        val results = filterBooks("Clean Code")
        assertEquals(1, results.size)
        assertEquals("Clean Code", results.first().title)
        assertEquals("Robert C. Martin", results.first().author)
    }

    @Test
    fun testSearchByTitle_partialMatchCaseInsensitive() {
        val results = filterBooks("algorithms")
        assertEquals(1, results.size)
        assertEquals("Introduction to Algorithms", results.first().title)
    }

    @Test
    fun testSearchByAuthor_partialMatch() {
        val results = filterBooks("Martin")
        assertEquals(1, results.size)
        assertEquals("Clean Code", results.first().title)
    }

    @Test
    fun testSearchWithEmptyQuery_returnsAllBooks() {
        val results = filterBooks("")
        assertEquals(books.size, results.size)
    }

    @Test
    fun testSearchNoMatch_returnsEmptyList() {
        val results = filterBooks("Nonexistent Random Title 12345")
        assertTrue(results.isEmpty())
    }

    @Test
    fun testFilterByCategory_ComputerScience() {
        val results = filterBooks(query = "", category = "Computer Science")
        assertTrue(results.size >= 3)
        assertTrue(results.all { it.category == "Computer Science" })
    }

    @Test
    fun testFilterByAvailability_Available() {
        val results = filterBooks(query = "", availability = "Available")
        assertTrue(results.all { it.status == Status.Available })
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun testFilterByAvailability_Borrowed() {
        val results = filterBooks(query = "", availability = "Borrowed")
        assertTrue(results.all { it.status == Status.Borrowed })
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun testCombinedSearchQueryAndCategory() {
        val results = filterBooks(query = "database", category = "Computer Science")
        assertEquals(1, results.size)
        assertEquals("Database System Concepts", results.first().title)
    }
}
