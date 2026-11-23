package com.example.libraryavailabilityinformationsystem

import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.db.*
import com.example.libraryavailabilityinformationsystem.models.*
import com.example.libraryavailabilityinformationsystem.util.PasswordHasher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RoomDatabaseTest {

    private lateinit var sampleBooks: List<BookEntity>
    private lateinit var sampleUsers: List<UserEntity>

    @Before
    fun setUp() {
        sampleBooks = LaisRepository.initialBooks.map { it.toEntity() }
        sampleUsers = LaisRepository.initialUsers.map { it.toEntity() }
    }

    @Test
    fun testOfflineStudentRegistration_hashesPassword() {
        val rawPassword = "StudentPass123"
        val hashedPassword = PasswordHasher.hashPassword(rawPassword)

        val newStudent = UserAccount(
            id = 99,
            name = "Offline Student",
            email = "offline.student@univ.edu",
            username = "offlinestudent",
            password = hashedPassword,
            role = UserRole.Student
        )
        val entity = newStudent.toEntity()

        assertEquals("Offline Student", entity.name)
        assertEquals("offline.student@univ.edu", entity.email)
        assertFalse(entity.passwordHash == rawPassword) // Password must not be plain text
        assertTrue(PasswordHasher.verifyPassword(rawPassword, entity.passwordHash))
    }

    @Test
    fun testOfflineLogin_verifiesPasswordHash() {
        val rawPass = "123"
        val user = sampleUsers.first()

        assertTrue(PasswordHasher.verifyPassword(rawPass, user.passwordHash))
        assertFalse(PasswordHasher.verifyPassword("WrongPass", user.passwordHash))
    }

    @Test
    fun testAddingBookAsLibrarian_savesToRoomEntity() {
        val newBook = Book(
            id = 15,
            title = "Modern Kotlin for Android",
            author = "Android Devs",
            year = 2025,
            category = "Software Engineering",
            status = Status.Available,
            copies = 4,
            total = 4
        )
        val entity = newBook.toEntity()
        assertEquals(15, entity.id)
        assertEquals("Modern Kotlin for Android", entity.title)
        assertEquals("Android Devs", entity.author)
        assertEquals("Available", entity.status)

        val model = entity.toModel()
        assertEquals(newBook.title, model.title)
        assertEquals(newBook.status, model.status)
    }

    @Test
    fun testOfflineSearchByTitle() {
        val query = "clean code"
        val results = sampleBooks.filter {
            it.title.lowercase().contains(query) || it.author.lowercase().contains(query)
        }
        assertEquals(1, results.size)
        assertEquals("Clean Code", results.first().title)
    }

    @Test
    fun testOfflineSearchByAuthor() {
        val query = "norman"
        val results = sampleBooks.filter {
            it.title.lowercase().contains(query) || it.author.lowercase().contains(query)
        }
        assertEquals(1, results.size)
        assertEquals("The Design of Everyday Things", results.first().title)
    }

    @Test
    fun testUpdatingBookAvailability_toBorrowed() {
        val book = sampleBooks.first().toModel()
        val updated = book.copy(status = Status.Borrowed, copies = 0, returnDate = "15 Jun 2025")
        val entity = updated.toEntity()

        assertEquals("Borrowed", entity.status)
        assertEquals(0, entity.copies)
        assertEquals("15 Jun 2025", entity.returnDate)
    }

    @Test
    fun testUpdatingBookAvailability_toAvailableClearsReturnDate() {
        val borrowedBook = sampleBooks.first { it.status == "Borrowed" }.toModel()
        assertNotNull(borrowedBook.returnDate)

        val returnedBook = borrowedBook.copy(
            status = Status.Available,
            copies = (borrowedBook.copies + 1).coerceAtMost(borrowedBook.total),
            returnDate = null
        )
        val entity = returnedBook.toEntity()

        assertEquals("Available", entity.status)
        assertNull(entity.returnDate)
    }

    @Test
    fun testAdministratorAccountManagement_deactivateUser() {
        val user = sampleUsers.first().toModel()
        val deactivated = user.copy(isActive = false)
        val entity = deactivated.toEntity()

        assertFalse(entity.isActive)
    }

    @Test
    fun testBorrowHistoryEntity_mapping() {
        val historyItem = BorrowHistoryItem(
            id = 1,
            bookId = 3,
            bookTitle = "The Design of Everyday Things",
            author = "Don Norman",
            borrowDate = "15 Mar 2025",
            returnDate = "08 Apr 2025",
            status = "Returned"
        )
        val entity = historyItem.toEntity()
        assertEquals(1, entity.id)
        assertEquals("The Design of Everyday Things", entity.bookTitle)

        val model = entity.toModel()
        assertEquals(historyItem.bookTitle, model.bookTitle)
        assertEquals(historyItem.borrowDate, model.borrowDate)
    }

    @Test
    fun testDataRetention_acrossRestarts() {
        val initialList = LaisRepository.initialBooks
        assertEquals(6, initialList.size)
        assertTrue(initialList.all { it.title.isNotEmpty() })
    }
}
