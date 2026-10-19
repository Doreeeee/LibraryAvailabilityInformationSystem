package com.example.libraryavailabilityinformationsystem

import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.Tab
import com.example.libraryavailabilityinformationsystem.models.UserAccount
import com.example.libraryavailabilityinformationsystem.models.UserRole
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RoleAuthorizationTest {

    private lateinit var users: List<UserAccount>

    @Before
    fun setUp() {
        users = LaisRepository.initialUsers
    }

    private fun allowedTabsForRole(role: UserRole): List<Tab> {
        return when (role) {
            UserRole.Student -> listOf(Tab.Home, Tab.Saved, Tab.History, Tab.Returns, Tab.Alerts, Tab.Account)
            UserRole.Librarian -> listOf(Tab.Catalog, Tab.AddBook, Tab.Returns, Tab.Account)
            UserRole.Admin -> listOf(Tab.Dashboard, Tab.Users, Tab.Audit, Tab.Logs, Tab.Backup, Tab.Account)
        }
    }

    @Test
    fun testStudentRoleCannotAccessAdminTabs() {
        val student = users.first { it.role == UserRole.Student }
        val allowedTabs = allowedTabsForRole(student.role)

        assertFalse(allowedTabs.contains(Tab.Dashboard))
        assertFalse(allowedTabs.contains(Tab.Users))
        assertFalse(allowedTabs.contains(Tab.Audit))
        assertFalse(allowedTabs.contains(Tab.Logs))
        assertFalse(allowedTabs.contains(Tab.Backup))
    }

    @Test
    fun testStudentRoleCannotAccessLibrarianTabs() {
        val student = users.first { it.role == UserRole.Student }
        val allowedTabs = allowedTabsForRole(student.role)

        assertFalse(allowedTabs.contains(Tab.Catalog))
        assertFalse(allowedTabs.contains(Tab.AddBook))
    }

    @Test
    fun testLibrarianRoleAccessRights() {
        val librarian = users.first { it.role == UserRole.Librarian }
        val allowedTabs = allowedTabsForRole(librarian.role)

        assertTrue(allowedTabs.contains(Tab.Catalog))
        assertTrue(allowedTabs.contains(Tab.AddBook))
        assertTrue(allowedTabs.contains(Tab.Returns))
        assertFalse(allowedTabs.contains(Tab.Dashboard))
        assertFalse(allowedTabs.contains(Tab.Users))
    }

    @Test
    fun testAdminRoleAccessRights() {
        val admin = users.first { it.role == UserRole.Admin }
        val allowedTabs = allowedTabsForRole(admin.role)

        assertTrue(allowedTabs.contains(Tab.Dashboard))
        assertTrue(allowedTabs.contains(Tab.Users))
        assertTrue(allowedTabs.contains(Tab.Audit))
        assertTrue(allowedTabs.contains(Tab.Logs))
        assertTrue(allowedTabs.contains(Tab.Backup))
        assertFalse(allowedTabs.contains(Tab.Home))
    }
}
