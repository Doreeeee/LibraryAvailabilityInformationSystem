package com.example.libraryavailabilityinformationsystem.data

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.libraryavailabilityinformationsystem.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object LaisRepository {
    private const val TAG = "LaisRepository"

    // Safe Firebase Accessors
    private val auth: FirebaseAuth? by lazy {
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }
    private val db: FirebaseFirestore? by lazy {
        try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
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

    // ---------- Firebase Sync & Operations ----------

    fun observeBooksFromFirestore(onBooksUpdated: (List<Book>) -> Unit) {
        val firestoreDb = db ?: return onBooksUpdated(initialBooks)

        firestoreDb.collection("books").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                Log.e(TAG, "Error fetching books from Firestore: ${error?.message}")
                onBooksUpdated(initialBooks)
                return@addSnapshotListener
            }

            if (snapshot.isEmpty) {
                seedInitialBooksToFirestore()
                onBooksUpdated(initialBooks)
            } else {
                val parsedBooks = snapshot.documents.mapNotNull { doc ->
                    try {
                        parseBookFromDoc(doc)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (parsedBooks.isNotEmpty()) {
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
        val firestoreDb = db ?: return onUsersUpdated(initialUsers)

        firestoreDb.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                onUsersUpdated(initialUsers)
                return@addSnapshotListener
            }

            if (snapshot.isEmpty) {
                seedInitialUsersToFirestore()
                onUsersUpdated(initialUsers)
            } else {
                val parsedUsers = snapshot.documents.mapNotNull { doc ->
                    try {
                        parseUserFromDoc(doc)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (parsedUsers.isNotEmpty()) {
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

    private fun seedInitialUsersToFirestore() {
        val firestoreDb = db ?: return
        initialUsers.forEach { user ->
            val userMap = hashMapOf(
                "id" to user.id,
                "userId" to user.id,
                "uid" to user.id.toString(),
                "name" to user.name,
                "fullName" to user.name,
                "email" to user.email,
                "username" to user.username,
                "password" to user.password,
                "role" to user.role.name.lowercase(),
                "studentId" to user.studentId,
                "department" to user.department,
                "isActive" to user.isActive,
                "createdAt" to user.createdAt
            )
            firestoreDb.collection("users").document(user.id.toString()).set(userMap, SetOptions.merge())
        }
    }

    val registeredUsers = mutableListOf<UserAccount>()

    fun authenticateWithFirebase(
        usernameInput: String,
        passwordInput: String,
        activeUsersList: List<UserAccount> = emptyList(),
        onResult: (UserAccount?, String?) -> Unit
    ) {
        val input = usernameInput.trim()
        val pass = passwordInput.trim()

        val firestoreDb = db
        val firebaseAuth = auth

        val allLocalUsers = activeUsersList + registeredUsers + initialUsers
        val localMatch = allLocalUsers.find { u ->
            (u.email.equals(input, ignoreCase = true) || u.username.equals(input, ignoreCase = true)) &&
            (u.password.isEmpty() || u.password == pass)
        }

        if (localMatch != null) {
            if (!localMatch.isActive) {
                onResult(null, "Your account has been deactivated by administrator.")
            } else {
                onResult(localMatch, null)
            }
            if (firebaseAuth != null) {
                try {
                    val isEmail = input.contains("@")
                    val authEmail = if (isEmail) input else localMatch.email
                    firebaseAuth.signInWithEmailAndPassword(authEmail, pass)
                } catch (e: Exception) {
                    Log.d(TAG, "Async sign in sync: ${e.message}")
                }
            }
            return
        }

        val isEmail = input.contains("@")
        val authEmail = if (isEmail) input else "$input@univ.edu"

        if (firebaseAuth == null || firestoreDb == null) {
            onResult(null, "Invalid credentials")
            return
        }

        firebaseAuth.signInWithEmailAndPassword(authEmail, pass)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                Log.d(TAG, "Firebase Auth sign-in successful for UID: $uid. Querying Firestore users/$uid...")

                firestoreDb.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        if (doc != null && doc.exists()) {
                            val user = parseUserFromDoc(doc)
                            if (!user.isActive) {
                                onResult(null, "Your account has been deactivated by administrator.")
                            } else {
                                registeredUsers.add(user)
                                onResult(user, null)
                            }
                        } else {
                            firestoreDb.collection("users")
                                .whereEqualTo("email", authEmail)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    val matchedDoc = snapshot?.documents?.firstOrNull()
                                    if (matchedDoc != null) {
                                        val u = parseUserFromDoc(matchedDoc)
                                        if (!u.isActive) {
                                            onResult(null, "Your account has been deactivated by administrator.")
                                        } else {
                                            registeredUsers.add(u)
                                            onResult(u, null)
                                        }
                                    } else {
                                        val fallbackUser = UserAccount(
                                            id = uid.hashCode(),
                                            name = authEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                                            email = authEmail,
                                            username = authEmail.substringBefore("@"),
                                            password = pass,
                                            role = UserRole.Student,
                                            uid = uid
                                        )
                                        registeredUsers.add(fallbackUser)
                                        onResult(fallbackUser, null)
                                    }
                                }
                                .addOnFailureListener {
                                    onResult(null, "Invalid credentials")
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Firestore lookup failed for users/$uid: ${e.localizedMessage}", e)
                        onResult(null, "Invalid credentials")
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Firebase Auth sign-in failed: ${e.localizedMessage}", e)
                firestoreDb.collection("users")
                    .whereEqualTo("email", authEmail)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val matchedDoc = snapshot?.documents?.find { d ->
                            val uPass = d.getString("password") ?: ""
                            uPass.isEmpty() || uPass == pass
                        }
                        if (matchedDoc != null) {
                            val user = parseUserFromDoc(matchedDoc)
                            if (!user.isActive) {
                                onResult(null, "Your account has been deactivated by administrator.")
                            } else {
                                registeredUsers.add(user)
                                onResult(user, null)
                            }
                        } else {
                            onResult(null, "Invalid credentials")
                        }
                    }
                    .addOnFailureListener {
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
        val firestoreDb = db
        val firebaseAuth = auth

        val cleanEmail = email.trim()
        val cleanPass = password.trim()
        val fallbackId = Math.abs(cleanEmail.hashCode())
        val generatedUid = "user_$fallbackId"

        val localUser = UserAccount(
            id = fallbackId,
            name = name,
            email = cleanEmail,
            username = cleanEmail.substringBefore("@"),
            password = cleanPass,
            role = role,
            uid = generatedUid
        )

        // Save profile to Firestore immediately
        saveUserToFirestore(localUser)
        registeredUsers.add(localUser)

        if (firebaseAuth == null || firestoreDb == null) {
            Log.w(TAG, "Firebase services not initialized, registering locally.")
            onResult(localUser, null)
            return
        }

        Log.d(TAG, "Creating Firebase Auth user for $cleanEmail...")

        firebaseAuth.createUserWithEmailAndPassword(cleanEmail, cleanPass)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                val uid = firebaseUser?.uid ?: generatedUid

                Log.d(TAG, "Firebase Auth account created successfully. UID: $uid")

                val now = System.currentTimeMillis()
                val userData = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "fullName" to name,
                    "email" to cleanEmail,
                    "phoneNumber" to "+1 (555) 019-2834",
                    "studentId" to "STU-2025-${uid.take(4)}",
                    "role" to "student",
                    "isActive" to true,
                    "createdAt" to now,
                    "updatedAt" to now
                )

                Log.d(TAG, "Writing Firestore document to path: users/$uid...")

                firestoreDb.collection("users")
                    .document(uid)
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "Firestore document users/$uid created successfully!")
                        val registeredUser = localUser.copy(uid = uid)
                        registeredUsers.add(registeredUser)
                        onResult(registeredUser, null)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Firestore write error for users/$uid (${e.message}), completing registration.")
                        onResult(localUser, null)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Firebase Auth registration notice (${e.localizedMessage}), proceeding with created user profile.")
                onResult(localUser, null)
            }
    }

    fun saveBookToFirestore(book: Book, onComplete: ((Boolean) -> Unit)? = null) {
        val firestoreDb = db
        if (firestoreDb == null) {
            onComplete?.invoke(true)
            return
        }

        val now = System.currentTimeMillis()
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
            "total" to book.total,
            "totalCopies" to book.total,
            "numberOfCopies" to book.total,
            "returnDate" to book.returnDate,
            "expectedReturnDate" to book.returnDate,
            "isOutdated" to book.isOutdated,
            "isbn" to book.isbn,
            "description" to book.description,
            "createdBy" to book.createdBy,
            "createdAt" to book.createdAt,
            "updatedAt" to now,
            "isActive" to book.isActive
        )

        firestoreDb.collection("books").document(book.id.toString())
            .set(bookMap, SetOptions.merge())
            .addOnSuccessListener {
                logActivity(
                    performedBy = book.createdBy,
                    performedByRole = "librarian",
                    action = if (book.id == 0) "ADD_BOOK" else "UPDATE_BOOK",
                    targetBookId = book.id.toString(),
                    description = "Librarian updated book record for '${book.title}'"
                )
                // Check pending book notifications if book became Available
                if (book.status == Status.Available) {
                    processPendingBookNotifications(book.id, book.title)
                }
                onComplete?.invoke(true)
            }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    fun requestBookNotification(
        studentUid: String,
        bookId: Int,
        bookTitle: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val firestoreDb = db
        if (firestoreDb == null) {
            onResult(true, null)
            return
        }

        // Query bookNotifications to prevent duplicate request
        firestoreDb.collection("bookNotifications")
            .whereEqualTo("studentUid", studentUid)
            .whereEqualTo("bookId", bookId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    onResult(false, "Notification alert is already active for this book.")
                } else {
                    val notifId = "notif_${System.currentTimeMillis()}"
                    val notifData = hashMapOf(
                        "notificationId" to notifId,
                        "studentUid" to studentUid,
                        "bookId" to bookId,
                        "bookTitle" to bookTitle,
                        "requestedAt" to System.currentTimeMillis(),
                        "notified" to false,
                        "notificationSentAt" to null
                    )
                    firestoreDb.collection("bookNotifications").document(notifId)
                        .set(notifData)
                        .addOnSuccessListener { onResult(true, null) }
                        .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.localizedMessage)
            }
    }

    private fun processPendingBookNotifications(bookId: Int, bookTitle: String) {
        val firestoreDb = db ?: return
        firestoreDb.collection("bookNotifications")
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("notified", false)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    snapshot.documents.forEach { doc ->
                        doc.reference.update(
                            "notified", true,
                            "notificationSentAt", System.currentTimeMillis()
                        )
                    }
                }
            }
    }

    fun logActivity(
        performedBy: String,
        performedByRole: String,
        action: String,
        targetBookId: String? = null,
        targetUserId: String? = null,
        description: String
    ) {
        val firestoreDb = db ?: return
        val logId = "log_${System.currentTimeMillis()}"
        val logMap = hashMapOf(
            "logId" to logId,
            "performedBy" to performedBy,
            "performedByRole" to performedByRole,
            "action" to action,
            "targetBookId" to targetBookId,
            "targetUserId" to targetUserId,
            "description" to description,
            "timestamp" to System.currentTimeMillis()
        )
        firestoreDb.collection("activityLogs").document(logId).set(logMap, SetOptions.merge())
    }

    fun saveUserToFirestore(user: UserAccount, onComplete: ((Boolean) -> Unit)? = null) {
        val firestoreDb = db
        if (firestoreDb == null) {
            onComplete?.invoke(true)
            return
        }

        val docId = if (!user.uid.isNullOrEmpty()) user.uid!! else if (user.email.isNotBlank()) user.email.replace(".", "_") else user.id.toString()
        val userMap = hashMapOf(
            "uid" to docId,
            "id" to user.id,
            "userId" to user.id,
            "name" to user.name,
            "fullName" to user.name,
            "email" to user.email,
            "username" to user.username,
            "role" to user.role.name.lowercase(),
            "studentId" to user.studentId,
            "department" to user.department,
            "isActive" to user.isActive,
            "createdAt" to user.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )

        firestoreDb.collection("users").document(docId)
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                onComplete?.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save user to Firestore: ${e.message}")
                onComplete?.invoke(false)
            }
    }

    fun deleteUserFromFirestore(userId: Int, onComplete: ((Boolean) -> Unit)? = null) {
        val firestoreDb = db
        if (firestoreDb == null) {
            onComplete?.invoke(true)
            return
        }

        firestoreDb.collection("users").document(userId.toString())
            .delete()
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }
}
