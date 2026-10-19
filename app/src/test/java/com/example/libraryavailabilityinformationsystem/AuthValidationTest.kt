package com.example.libraryavailabilityinformationsystem

import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.UserAccount
import com.example.libraryavailabilityinformationsystem.models.UserRole
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthValidationTest {

    private lateinit var usersList: MutableList<UserAccount>

    @Before
    fun setUp() {
        usersList = LaisRepository.initialUsers.toMutableList()
    }

    private fun authenticate(inputUser: String, inputPass: String): UserAccount? {
        val u = inputUser.trim()
        val p = inputPass.trim()
        return usersList.find {
            (it.username.equals(u, ignoreCase = true) || it.email.equals(u, ignoreCase = true)) &&
            it.password == p
        }
    }

    @Test
    fun testStudentLogin_validCredentials_Doe() {
        val user = authenticate("Doe", "123")
        assertNotNull(user)
        assertEquals("Doe", user!!.username)
        assertEquals(UserRole.Student, user.role)
        assertTrue(user.isActive)
    }

    @Test
    fun testStaffLogin_validCredentials_lib() {
        val user = authenticate("lib", "123")
        assertNotNull(user)
        assertEquals("lib", user!!.username)
        assertEquals(UserRole.Librarian, user.role)
        assertTrue(user.isActive)
    }

    @Test
    fun testAdminLogin_validCredentials_admin() {
        val user = authenticate("admin", "admin")
        assertNotNull(user)
        assertEquals("admin", user!!.username)
        assertEquals(UserRole.Admin, user.role)
        assertTrue(user.isActive)
    }

    @Test
    fun testLogin_invalidPassword_fails() {
        val user = authenticate("Doe", "wrongpass")
        assertNull(user)
    }

    @Test
    fun testLogin_invalidUsername_fails() {
        val user = authenticate("NonexistentUser", "123")
        assertNull(user)
    }

    @Test
    fun testLogin_deactivatedAccount_preventsAccess() {
        val index = usersList.indexOfFirst { it.username == "Doe" }
        usersList[index] = usersList[index].copy(isActive = false)

        val user = authenticate("Doe", "123")
        assertNotNull(user)
        assertFalse(user!!.isActive)
    }

    @Test
    fun testUserRegistration_addsNewUser() {
        val newUser = UserAccount(
            id = usersList.size + 1,
            name = "Test Student",
            email = "test.student@univ.edu",
            username = "teststudent",
            password = "password123",
            role = UserRole.Student,
            studentId = "STU-2025-999"
        )
        usersList.add(newUser)

        val authenticated = authenticate("teststudent", "password123")
        assertNotNull(authenticated)
        assertEquals("Test Student", authenticated!!.name)
        assertEquals(UserRole.Student, authenticated.role)
    }
}
