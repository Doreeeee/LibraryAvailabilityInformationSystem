package com.example.libraryavailabilityinformationsystem.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.example.libraryavailabilityinformationsystem.models.*

object LaisRepository {
    val initialUsers = listOf(
        UserAccount(1, "John Doe", "doe@univ.edu", "Doe", "123", UserRole.Student, "STU-2025-0148", "Computer Science", "+1 (555) 019-2834"),
        UserAccount(2, "Sarah Jenkins", "lib@univ.edu", "lib", "123", UserRole.Librarian, "LIB-8802", "Library Administration", "+1 (555) 018-9922"),
        UserAccount(3, "David Vance", "admin@univ.edu", "admin", "admin", UserRole.Admin, "ADM-1001", "IT & System Admin", "+1 (555) 017-3344"),
        UserAccount(4, "Emma Watson", "e.watson@univ.edu", "emma", "123", UserRole.Student, "STU-2025-0210", "Electrical Engineering", "+1 (555) 014-5511"),
        UserAccount(5, "Michael Chen", "m.chen@univ.edu", "mchen", "123", UserRole.Librarian, "LIB-9104", "Library Circulation", "+1 (555) 012-7788")
    )

    val initialBooks = listOf(
        Book(
            id = 1,
            title = "Introduction to Algorithms",
            author = "Thomas H. Cormen",
            year = 2022,
            category = "Computer Science",
            status = Status.Available,
            copies = 3,
            total = 5,
            location = "Main Library - Floor 2",
            shelf = "CS-204 A",
            returnDate = null,
            coverColors = listOf(Color(0xFF0E3768), Color(0xFF081B31))
        ),
        Book(
            id = 2,
            title = "Clean Code",
            author = "Robert C. Martin",
            year = 2008,
            category = "Software Engineering",
            status = Status.Borrowed,
            copies = 0,
            total = 2,
            location = "Main Library - Floor 2",
            shelf = "SE-102 B",
            returnDate = "24 May 2025",
            coverColors = listOf(Color(0xFF1D5E77), Color(0xFF0D2C3B))
        ),
        Book(
            id = 3,
            title = "The Design of Everyday Things",
            author = "Don Norman",
            year = 2013,
            category = "Design",
            status = Status.Available,
            copies = 1,
            total = 4,
            location = "Science Building - Floor 1",
            shelf = "DES-301",
            returnDate = null,
            coverColors = listOf(Color(0xFFD04B2B), Color(0xFF642012))
        ),
        Book(
            id = 4,
            title = "Artificial Intelligence",
            author = "Stuart Russell & Peter Norvig",
            year = 2021,
            category = "Computer Science",
            status = Status.Borrowed,
            copies = 0,
            total = 3,
            location = "Main Library - Floor 3",
            shelf = "AI-405 C",
            returnDate = "02 June 2025",
            coverColors = listOf(Color(0xFF49438C), Color(0xFF1B1942))
        ),
        Book(
            id = 5,
            title = "Principles of Economics",
            author = "N. Gregory Mankiw",
            year = 2020,
            category = "Economics",
            status = Status.Unavailable,
            copies = 0,
            total = 0,
            location = "Social Sciences Library",
            shelf = "ECO-101",
            returnDate = null,
            coverColors = listOf(Color(0xFFA96623), Color(0xFF4F2E0F))
        ),
        Book(
            id = 6,
            title = "Database System Concepts",
            author = "Abraham Silberschatz",
            year = 2019,
            category = "Computer Science",
            status = Status.Available,
            copies = 2,
            total = 4,
            location = "Main Library - Floor 2",
            shelf = "CS-208 D",
            returnDate = null,
            coverColors = listOf(Color(0xFF135848), Color(0xFF072720))
        )
    )

    val initialActivities = listOf(
        SystemActivity(1, "10 mins ago", "Alex Morgan", "Searched for 'Algorithms'", "SEARCH"),
        SystemActivity(2, "25 mins ago", "Sarah Jenkins", "Updated return date for Clean Code", "BOOK_UPDATE"),
        SystemActivity(3, "1 hour ago", "Emma Watson", "Set notification for Artificial Intelligence", "RESERVATION"),
        SystemActivity(4, "2 hours ago", "David Vance", "Deactivated user STU-2024-0012", "USER_MGMT"),
        SystemActivity(5, "3 hours ago", "Alex Morgan", "Logged into Student Portal", "LOGIN")
    )

    val initialLogs = listOf(
        SystemLog(101, "2025-05-20 09:14:02", "INFO", "User alex (STU-2025-0148) authenticated successfully", "AUTH_SERVICE"),
        SystemLog(102, "2025-05-20 08:45:11", "ADMIN", "Librarian s.jenkins updated Book ID #2 return date to 2025-05-24", "BOOK_SERVICE"),
        SystemLog(103, "2025-05-20 07:30:00", "INFO", "Automatic nightly database index optimization completed", "DB_WORKER"),
        SystemLog(104, "2025-05-19 22:10:45", "WARN", "3 consecutive failed login attempts for user mchen", "SECURITY_GATEWAY"),
        SystemLog(105, "2025-05-19 18:00:12", "ADMIN", "System admin d.vance created system backup snapshot #20250519", "BACKUP_MGR")
    )

    val initialBackups = listOf(
        BackupRecord(1, "20 May 2025, 08:00 AM", "4.2 MB", 148),
        BackupRecord(2, "13 May 2025, 08:00 AM", "4.1 MB", 142),
        BackupRecord(3, "06 May 2025, 08:00 AM", "3.9 MB", 135)
    )

    val initialBorrowHistory = listOf(
        BorrowHistoryItem(1, 3, "The Design of Everyday Things", "Don Norman", "15 Mar 2025", "08 Apr 2025", "Returned"),
        BorrowHistoryItem(2, 5, "Principles of Economics", "N. Gregory Mankiw", "02 Jan 2025", "14 Feb 2025", "Returned")
    )

    val categories = listOf("All", "Computer Science", "Software Engineering", "Design", "Economics")
}
