package com.example.libraryavailabilityinformationsystem.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// ---------- Colors (from index.css & design spec) ----------
val Ink = Color(0xFF12233F)
val InkSoft = Color(0xFF68788C)
val Forest = Color(0xFF1565D8)
val Mint = Color(0xFFDCECFF)
val MintLight = Color(0xFFEEF6FF)
val Cream = Color(0xFFFFF4E8)
val Paper = Color(0xFFFBFDFF)
val Coral = Color(0xFFFF7448)
val Line = Color(0xFFDFE8F1)
val Acid = Color(0xFFFFD166)
val Field = Color(0xFFF3F8FD)
val Serif = FontFamily.Serif

// ---------- Enums ----------
enum class UserRole(val label: String) {
    Student("Student"),
    Librarian("Librarian Staff"),
    Admin("Administrator")
}

enum class Status(val label: String) {
    Available("Available"),
    Borrowed("Borrowed"),
    Unavailable("Unavailable")
}

enum class Flow {
    Splash, Login, Welcome, App
}

enum class Tab {
    // Student Tabs
    Home, Saved, History, Returns, Alerts, Account,
    // Librarian Tabs
    Catalog, AddBook,
    // Admin Tabs
    Dashboard, Users, Audit, Logs, Backup
}

enum class HomeMode {
    Smart, Search, Availability
}

// ---------- Data Models ----------
data class UserAccount(
    val id: Int,
    val name: String,
    val email: String,
    val username: String,
    val password: String = "",
    val role: UserRole,
    val studentId: String? = null,
    val department: String? = "Computer Science",
    val phone: String? = "+1 (555) 019-2834",
    val isActive: Boolean = true,
    val createdAt: String = "15 Jan 2025",
    val uid: String? = null
)

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: Int = 2024,
    val category: String = "General",
    val status: Status = Status.Available,
    val copies: Int = 1,
    val total: Int = 1,
    val location: String = "",
    val shelf: String = "",
    val returnDate: String? = null,
    val coverColors: List<Color> = listOf(Color(0xFF0E3768), Color(0xFF081B31)),
    val isOutdated: Boolean = false,
    val isbn: String = "978-0131103627",
    val description: String = "Comprehensive book on fundamentals and core principles.",
    val createdBy: String = "Librarian Staff",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

data class BorrowHistoryItem(
    val id: Int,
    val bookId: Int,
    val bookTitle: String,
    val author: String,
    val borrowDate: String,
    val returnDate: String,
    val status: String
)

data class SystemActivity(
    val id: Int,
    val timestamp: String,
    val user: String,
    val action: String,
    val type: String
)

data class SystemLog(
    val id: Int,
    val timestamp: String,
    val level: String,
    val message: String,
    val source: String
)

data class BackupRecord(
    val id: Int,
    val timestamp: String,
    val size: String,
    val recordCount: Int
)

data class BookNotification(
    val notificationId: String,
    val studentUid: String,
    val bookId: Int,
    val bookTitle: String,
    val requestedAt: Long = System.currentTimeMillis(),
    val notified: Boolean = false,
    val notificationSentAt: Long? = null
)

data class ActivityLog(
    val logId: String,
    val performedBy: String,
    val performedByRole: String,
    val action: String,
    val targetUserId: String? = null,
    val targetBookId: String? = null,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
