package com.example.libraryavailabilityinformationsystem.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.libraryavailabilityinformationsystem.db.*
import com.example.libraryavailabilityinformationsystem.models.*
import com.example.libraryavailabilityinformationsystem.sync.SyncWorker
import com.example.libraryavailabilityinformationsystem.util.PasswordHasher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object LaisRepository {
    private const val TAG = "LaisRepository"

    @Volatile
    private var roomDb: LaisDatabase? = null

    @Volatile
    private var appContext: Context? = null

    // Safe Firebase Accessors
    private val auth: FirebaseAuth? by lazy {
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }
    private val db: FirebaseFirestore? by lazy {
        try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
    }

    fun initRoomDatabase(context: Context) {
        appContext = context.applicationContext
        if (roomDb == null) {
            synchronized(this) {
                if (roomDb == null) {
                    val instance = LaisDatabase.getInstance(context)
                    roomDb = instance
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val existingBooks = instance.bookDao().getAllBooks()
                            if (existingBooks.isEmpty()) {
                                instance.bookDao().insertBooks(initialBooks.map { it.toEntity() })
                            }
                            val existingUsers = instance.userDao().getAllUsers()
                            if (existingUsers.isEmpty()) {
                                instance.userDao().insertUsers(initialUsers.map { it.toEntity() })
                            }
                            val existingHistory = instance.borrowHistoryDao().getAllHistory()
                            if (existingHistory.isEmpty()) {
                                instance.borrowHistoryDao().insertHistory(initialBorrowHistory.map { it.toEntity() })
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error populating Room database: ${e.message}", e)
                        }
                    }
                }
            }
        }
    }

    fun getBooksFromRoom(): List<Book>? {
        return roomDb?.bookDao()?.getAllBooks()?.map { it.toModel() }
    }

    fun getUsersFromRoom(): List<UserAccount>? {
        return roomDb?.userDao()?.getAllUsers()?.map { it.toModel() }
    }

    fun searchBooksOffline(query: String, onResult: (List<Book>) -> Unit) {
        val instance = roomDb
        if (instance != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val q = query.trim()
                    val entities = if (q.isEmpty()) instance.bookDao().getAllBooks() else instance.bookDao().searchBooks(q)
                    val books = entities.map { it.toModel() }
                    withContext(Dispatchers.Main) {
                        onResult(if (books.isNotEmpty()) books else initialBooks)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onResult(initialBooks)
                    }
                }
            }
        } else {
            val q = query.trim().lowercase()
            val filtered = initialBooks.filter {
                q.isEmpty() || it.title.lowercase().contains(q) || it.author.lowercase().contains(q)
            }
            onResult(filtered)
        }
    }

    val initialUsers = listOf(
        UserAccount(1, "John Doe", "doe@univ.edu", "Doe", "123", UserRole.Student, "STU-2025-0148", "Computer Science", "+1 (555) 019-2834"),
        UserAccount(2, "Sarah Jenkins", "lib@univ.edu", "lib", "123", UserRole.Librarian, "LIB-8802", "Library Administration", "+1 (555) 018-9922"),
        UserAccount(3, "David Vance", "admin@univ.edu", "admin", "admin", UserRole.Admin, "ADM-1001", "IT & System Admin", "+1 (555) 017-3344"),
        UserAccount(4, "Emma Watson", "e.watson@univ.edu", "emma", "123", UserRole.Student, "STU-2025-0210", "Electrical Engineering", "+1 (555) 014-5511"),
        UserAccount(5, "Michael Chen", "m.chen@univ.edu", "mchen", "123", UserRole.Librarian, "LIB-9104", "Library Circulation", "+1 (555) 012-7788"),
        UserAccount(6, "Librarian 01", "librarian01@univ.edu", "librarian01", "Library123", UserRole.Librarian, "LIB-0001", "Circulation Desk"),
        UserAccount(7, "Administrator 01", "admin01@univ.edu", "admin01", "Admin123", UserRole.Admin, "ADM-0001", "System Control")
    )

    private fun getUpcomingDateString(daysFromNow: Int): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_MONTH, daysFromNow)
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return sdf.format(cal.time)
    }

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
            returnDate = getUpcomingDateString(7),
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
            returnDate = getUpcomingDateString(14),
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

    val registeredUsers = mutableListOf<UserAccount>()
    val notificationRequests = mutableListOf<BookNotification>()
    val activityLogsList = mutableListOf<ActivityLog>()

    fun observeBooksFromFirestore(onBooksUpdated: (List<Book>) -> Unit) {
        val firestoreDb = db
        if (firestoreDb == null) {
            val instance = roomDb
            if (instance != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val books = instance.bookDao().getAllBooks().map { it.toModel() }
                        withContext(Dispatchers.Main) {
                            onBooksUpdated(if (books.isNotEmpty()) books else initialBooks)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) { onBooksUpdated(initialBooks) }
                    }
                }
            } else {
                onBooksUpdated(initialBooks)
            }
            return
        }

        firestoreDb.collection("books").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                onBooksUpdated(initialBooks)
                return@addSnapshotListener
            }

            if (snapshot.isEmpty) {
                seedInitialBooksToFirestore()
                onBooksUpdated(initialBooks)
            } else {
                val parsedBooks = snapshot.documents.mapNotNull { doc ->
                    try { parseBookFromDoc(doc) } catch (e: Exception) { null }
                }
                if (parsedBooks.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            roomDb?.bookDao()?.insertBooks(parsedBooks.map { it.toEntity("SYNCED") })
                        } catch (e: Exception) {
                            Log.e(TAG, "Error caching Firestore books to Room: ${e.message}")
                        }
                    }
                    onBooksUpdated(parsedBooks)
                } else {
                    onBooksUpdated(initialBooks)
                }
            }
        }
    }

    private fun parseBookFromDoc(doc: DocumentSnapshot): Book {
        val id = (doc.getLong("id") ?: doc.getLong("bookId") ?: 0L).toInt()
        val title = doc.getString("title") ?: "Untitled"
        val author = doc.getString("author") ?: "Unknown Author"
        val year = (doc.getLong("year") ?: 2024L).toInt()
        val category = doc.getString("category") ?: "General"
        val statusStr = doc.getString("status") ?: "Available"
        val status = when (statusStr.uppercase()) {
            "BORROWED" -> Status.Borrowed
            "UNAVAILABLE" -> Status.Unavailable
            else -> Status.Available
        }
        val copies = (doc.getLong("copies") ?: doc.getLong("availableCopies") ?: doc.getLong("numberOfCopies") ?: 1L).toInt()
        val total = (doc.getLong("total") ?: doc.getLong("totalCopies") ?: doc.getLong("numberOfCopies") ?: 1L).toInt()
        val returnDate = doc.getString("returnDate") ?: doc.getString("expectedReturnDate")
        val isOutdated = doc.getBoolean("isOutdated") ?: false
        val isbn = doc.getString("isbn") ?: "978-0131103627"
        val description = doc.getString("description") ?: "Book details and info."
        val createdBy = doc.getString("createdBy") ?: "Librarian Staff"
        val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
        val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
        val isActive = doc.getBoolean("isActive") ?: true

        return Book(
            id = id,
            title = title,
            author = author,
            year = year,
            category = category,
            status = status,
            copies = copies,
            total = total,
            returnDate = returnDate,
            coverColors = listOf(Color(0xFF0E3768), Color(0xFF081B31)),
            isOutdated = isOutdated,
            isbn = isbn,
            description = description,
            createdBy = createdBy,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isActive = isActive
        )
    }

    private fun seedInitialBooksToFirestore() {
        val firestoreDb = db ?: return
        initialBooks.forEach { book ->
            val bookMap = hashMapOf(
                "id" to book.id,
                "bookId" to book.id,
                "title" to book.title,
                "author" to book.author,
                "year" to book.year,
                "category" to book.category,
                "status" to book.status.name,
                "copies" to book.copies,
                "availableCopies" to book.copies,
                "numberOfCopies" to book.total,
                "total" to book.total,
                "totalCopies" to book.total,
                "returnDate" to book.returnDate,
                "expectedReturnDate" to book.returnDate,
                "isOutdated" to book.isOutdated,
                "isbn" to book.isbn,
                "description" to book.description,
                "createdBy" to book.createdBy,
                "createdAt" to book.createdAt,
                "updatedAt" to book.updatedAt,
                "isActive" to book.isActive
            )
            firestoreDb.collection("books").document(book.id.toString()).set(bookMap, SetOptions.merge())
        }
    }

    fun observeUsersFromFirestore(onUsersUpdated: (List<UserAccount>) -> Unit) {
        val firestoreDb = db
        if (firestoreDb == null) {
            val instance = roomDb
            if (instance != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val users = instance.userDao().getAllUsers().map { it.toModel() }
                        withContext(Dispatchers.Main) {
                            val allUsers = (users + registeredUsers + initialUsers).distinctBy { it.id }
                            onUsersUpdated(allUsers)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) { onUsersUpdated(initialUsers + registeredUsers) }
                    }
                }
            } else {
                onUsersUpdated(initialUsers + registeredUsers)
            }
            return
        }

        firestoreDb.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                onUsersUpdated(initialUsers)
                return@addSnapshotListener
            }

            if (snapshot.isEmpty) {
                onUsersUpdated(initialUsers)
            } else {
                val parsedUsers = snapshot.documents.mapNotNull { doc ->
                    try { parseUserFromDoc(doc) } catch (e: Exception) { null }
                }
                if (parsedUsers.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            roomDb?.userDao()?.insertUsers(parsedUsers.map { it.toEntity("SYNCED") })
                        } catch (e: Exception) {
                            Log.e(TAG, "Error caching users to Room: ${e.message}")
                        }
                    }
                    onUsersUpdated(parsedUsers)
                } else {
                    onUsersUpdated(initialUsers)
                }
            }
        }
    }

    private fun parseUserFromDoc(doc: DocumentSnapshot): UserAccount {
        val id = (doc.getLong("id") ?: doc.getLong("userId") ?: doc.id.hashCode().toLong()).toInt()
        val name = doc.getString("name") ?: doc.getString("fullName") ?: "User"
        val email = doc.getString("email") ?: "user@univ.edu"
        val username = doc.getString("username") ?: email.substringBefore("@")
        val password = doc.getString("password") ?: "123"
        val roleStr = doc.getString("role") ?: "student"
        val role = when (roleStr.lowercase()) {
            "librarian", "librarian staff" -> UserRole.Librarian
            "admin", "administrator" -> UserRole.Admin
            else -> UserRole.Student
        }
        val studentId = doc.getString("studentId")
        val department = doc.getString("department")
        val isActive = doc.getBoolean("isActive") ?: true
        val uid = doc.getString("uid") ?: doc.id

        return UserAccount(
            id = id,
            name = name,
            email = email,
            username = username,
            password = password,
            role = role,
            studentId = studentId,
            department = department,
            isActive = isActive,
            uid = uid
        )
    }

    fun authenticateWithFirebase(
        usernameInput: String,
        passwordInput: String,
        activeUsersList: List<UserAccount> = emptyList(),
        onResult: (UserAccount?, String?) -> Unit
    ) {
        val input = usernameInput.trim()
        val pass = passwordInput.trim()

        val instance = roomDb
        if (instance != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userEntity = instance.userDao().getUserByEmailOrUsername(input, input)
                    if (userEntity != null) {
                        val isPassValid = PasswordHasher.verifyPassword(pass, userEntity.passwordHash)
                        withContext(Dispatchers.Main) {
                            if (!userEntity.isActive) {
                                onResult(null, "Your account has been deactivated by administrator.")
                            } else if (isPassValid) {
                                onResult(userEntity.toModel(), null)
                            } else {
                                onResult(null, "Invalid credentials")
                            }
                        }
                        return@launch
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error performing Room local authentication: ${e.message}")
                }

                // Fallback to in-memory list match
                val allLocalUsers = activeUsersList + registeredUsers + initialUsers
                val localMatch = allLocalUsers.find { u ->
                    (u.email.equals(input, ignoreCase = true) || u.username.equals(input, ignoreCase = true)) &&
                    (u.password.isEmpty() || PasswordHasher.verifyPassword(pass, u.password))
                }

                withContext(Dispatchers.Main) {
                    if (localMatch != null) {
                        if (!localMatch.isActive) {
                            onResult(null, "Your account has been deactivated by administrator.")
                        } else {
                            onResult(localMatch, null)
                        }
                    } else {
                        onResult(null, "Invalid credentials")
                    }
                }
            }
        } else {
            val allLocalUsers = activeUsersList + registeredUsers + initialUsers
            val localMatch = allLocalUsers.find { u ->
                (u.email.equals(input, ignoreCase = true) || u.username.equals(input, ignoreCase = true)) &&
                (u.password.isEmpty() || PasswordHasher.verifyPassword(pass, u.password))
            }

            if (localMatch != null) {
                if (!localMatch.isActive) {
                    onResult(null, "Your account has been deactivated by administrator.")
                } else {
                    onResult(localMatch, null)
                }
            } else {
                onResult(null, "Invalid credentials")
            }
        }
    }

    fun registerStudentWithFirebase(
        name: String,
        email: String,
        password: String,
        role: UserRole = UserRole.Student,
        onResult: (UserAccount?, String?) -> Unit
    ) {
        val cleanEmail = email.trim()
        val cleanPass = password.trim()
        val fallbackId = Math.abs(cleanEmail.hashCode())
        val generatedUid = "user_$fallbackId"
        val pwdHash = PasswordHasher.hashPassword(cleanPass)

        val localUser = UserAccount(
            id = fallbackId,
            name = name,
            email = cleanEmail,
            username = cleanEmail.substringBefore("@"),
            password = pwdHash,
            role = role,
            uid = generatedUid
        )

        saveUserToFirestore(localUser)
        onResult(localUser, null)
    }

    fun saveBookToFirestore(book: Book, onComplete: ((Boolean) -> Unit)? = null) {
        val now = System.currentTimeMillis()
        val isNew = (book.id == 0)
        val finalBookId = if (isNew) Math.abs(book.title.hashCode()) else book.id
        val bookToSave = book.copy(id = finalBookId, updatedAt = now)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dbInstance = roomDb
                if (dbInstance != null) {
                    dbInstance.bookDao().insertBook(bookToSave.toEntity("PENDING"))
                    val syncItem = PendingSyncEntity(
                        syncId = "sync_${now}_${finalBookId}",
                        entityType = "BOOK",
                        entityId = finalBookId.toString(),
                        operation = if (isNew) "INSERT" else "UPDATE",
                        timestamp = now,
                        status = "PENDING"
                    )
                    dbInstance.pendingSyncDao().insertPendingSync(syncItem)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving book to Room database: ${e.message}")
            }
        }

        // Schedule WorkManager network sync
        appContext?.let { ctx ->
            try {
                SyncWorker.scheduleNetworkSync(ctx)
            } catch (e: Exception) {
                Log.w(TAG, "WorkManager sync schedule warning: ${e.message}")
            }
        }

        logActivity(
            performedBy = bookToSave.createdBy,
            performedByRole = "librarian",
            action = if (isNew) "ADD_BOOK" else "UPDATE_BOOK",
            targetBookId = finalBookId.toString(),
            description = "Librarian updated book record for '${bookToSave.title}'"
        )
        onComplete?.invoke(true)
    }

    fun requestBookNotification(
        studentUid: String,
        bookId: Int,
        bookTitle: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val exists = notificationRequests.any { it.studentUid == studentUid && it.bookId == bookId }
        if (exists) {
            onResult(false, "Notification alert is already active for this book.")
        } else {
            val notif = BookNotification(
                notificationId = "notif_${System.currentTimeMillis()}",
                studentUid = studentUid,
                bookId = bookId,
                bookTitle = bookTitle
            )
            notificationRequests.add(notif)
            onResult(true, null)
        }
    }

    fun logActivity(
        performedBy: String,
        performedByRole: String,
        action: String,
        targetBookId: String? = null,
        targetUserId: String? = null,
        description: String = ""
    ) {
        val logId = "log_${System.currentTimeMillis()}"
        val logItem = ActivityLog(
            logId = logId,
            performedBy = performedBy,
            performedByRole = performedByRole,
            action = action,
            targetUserId = targetUserId,
            targetBookId = targetBookId,
            description = description
        )
        activityLogsList.add(logItem)
    }

    fun saveUserToFirestore(user: UserAccount, onComplete: ((Boolean) -> Unit)? = null) {
        val now = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dbInstance = roomDb
                if (dbInstance != null) {
                    val userEntity = user.toEntity("PENDING")
                    dbInstance.userDao().insertUser(userEntity)
                    val syncItem = PendingSyncEntity(
                        syncId = "sync_${now}_user_${user.id}",
                        entityType = "USER",
                        entityId = user.id.toString(),
                        operation = "INSERT",
                        timestamp = now,
                        status = "PENDING"
                    )
                    dbInstance.pendingSyncDao().insertPendingSync(syncItem)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving user to Room: ${e.message}")
            }
        }

        // Schedule WorkManager network sync
        appContext?.let { ctx ->
            try {
                SyncWorker.scheduleNetworkSync(ctx)
            } catch (e: Exception) {
                Log.w(TAG, "WorkManager sync schedule warning: ${e.message}")
            }
        }

        val idx = registeredUsers.indexOfFirst { it.id == user.id || (user.uid != null && it.uid == user.uid) }
        if (idx != -1) {
            registeredUsers[idx] = user
        } else {
            registeredUsers.add(user)
        }
        onComplete?.invoke(true)
    }

    fun deleteUserFromFirestore(userId: Int, onComplete: ((Boolean) -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                roomDb?.userDao()?.deleteUser(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting user from Room: ${e.message}")
            }
        }

        registeredUsers.removeAll { it.id == userId }
        onComplete?.invoke(true)
    }
}
