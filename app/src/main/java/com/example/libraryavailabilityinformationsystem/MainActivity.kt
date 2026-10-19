package com.example.libraryavailabilityinformationsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.*
import com.example.libraryavailabilityinformationsystem.ui.*
import com.example.libraryavailabilityinformationsystem.ui.theme.LibraryAvailabilityInformationSystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryAvailabilityInformationSystemTheme(dynamicColor = false) {
                LaisApp()
            }
        }
    }
}

@Composable
fun LaisApp() {
    // Persistent app state lists
    val usersList = remember { mutableStateListOf<UserAccount>().apply { addAll(LaisRepository.initialUsers) } }
    val booksList = remember { mutableStateListOf<Book>().apply { addAll(LaisRepository.initialBooks) } }
    val activitiesList = remember { mutableStateListOf<SystemActivity>().apply { addAll(LaisRepository.initialActivities) } }
    val logsList = remember { mutableStateListOf<SystemLog>().apply { addAll(LaisRepository.initialLogs) } }
    val backupsList = remember { mutableStateListOf<BackupRecord>().apply { addAll(LaisRepository.initialBackups) } }
    val historyList = remember { mutableStateListOf<BorrowHistoryItem>().apply { addAll(LaisRepository.initialBorrowHistory) } }

    var flow by rememberSaveable { mutableStateOf(Flow.Splash) }
    var currentUser by remember { mutableStateOf(usersList.first()) }
    var tab by rememberSaveable { mutableStateOf(Tab.Home) }
    var homeMode by rememberSaveable { mutableStateOf(HomeMode.Smart) }
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    var availability by rememberSaveable { mutableStateOf("All") }
    var selectedBookId by rememberSaveable { mutableStateOf<Int?>(null) }
    var editingBook by remember { mutableStateOf<Book?>(null) }
    var showAddBookModal by rememberSaveable { mutableStateOf(false) }

    val savedIds = remember { mutableStateListOf(3) }
    val alertIds = remember { mutableStateListOf<Int>() }

    fun toggleInList(list: MutableList<Int>, id: Int) {
        if (!list.remove(id)) list.add(id)
    }

    fun openFeature(t: Tab, avail: String = "All", mode: HomeMode = HomeMode.Smart) {
        tab = t
        homeMode = mode
        availability = avail
        query = ""
        category = "All"
        flow = Flow.App
    }

    val selectedBook = booksList.firstOrNull { it.id == selectedBookId }

    BackHandler(enabled = flow != Flow.Splash) {
        when {
            editingBook != null -> editingBook = null
            showAddBookModal -> showAddBookModal = false
            selectedBook != null -> selectedBookId = null
            flow == Flow.App -> flow = Flow.Welcome
            flow == Flow.Welcome -> flow = Flow.Login
            flow == Flow.Login -> flow = Flow.Splash
        }
    }

    Surface(Modifier.fillMaxSize(), color = Paper) {
        when {
            flow == Flow.Splash -> SplashScreen {
                flow = Flow.Login
            }

            flow == Flow.Login -> LoginRegisterScreen(
                usersList = usersList,
                onBack = { flow = Flow.Splash },
                onAuthSuccess = { authedUser ->
                    currentUser = authedUser
                    tab = when (authedUser.role) {
                        UserRole.Student -> Tab.Home
                        UserRole.Librarian -> Tab.Catalog
                        UserRole.Admin -> Tab.Dashboard
                    }
                    flow = Flow.Welcome

                    activitiesList.add(
                        0,
                        SystemActivity(
                            id = activitiesList.size + 1,
                            timestamp = "Just now",
                            user = authedUser.name,
                            action = "Logged in as ${authedUser.role.label}",
                            type = "LOGIN"
                        )
                    )
                },
                onRegisterUser = { newUser ->
                    usersList.add(newUser)
                    logsList.add(
                        0,
                        SystemLog(
                            id = logsList.size + 100,
                            timestamp = "Just now",
                            level = "INFO",
                            message = "Registered user ${newUser.username} (${newUser.role.label})",
                            source = "AUTH_GATEWAY"
                        )
                    )
                }
            )

            flow == Flow.Welcome -> WelcomeScreen(
                user = currentUser,
                onBack = { flow = Flow.Login },
                onOpenTab = ::openFeature
            )

            selectedBook != null -> DetailScreen(
                book = selectedBook,
                saved = selectedBook.id in savedIds,
                alert = selectedBook.id in alertIds,
                onBack = { selectedBookId = null },
                onToggleSaved = { toggleInList(savedIds, selectedBook.id) },
                onToggleAlert = { toggleInList(alertIds, selectedBook.id) }
            )

            else -> Column(Modifier.fillMaxSize().statusBarsPadding()) {
                AppBar(
                    user = currentUser,
                    onBack = { flow = Flow.Welcome },
                    onOpenAccount = { tab = Tab.Account },
                    onSwitchRole = {
                        // Switch role cycle for quick preview/testing
                        val nextRole = when (currentUser.role) {
                            UserRole.Student -> UserRole.Librarian
                            UserRole.Librarian -> UserRole.Admin
                            UserRole.Admin -> UserRole.Student
                        }
                        currentUser = usersList.firstOrNull { it.role == nextRole } ?: currentUser.copy(role = nextRole)
                        tab = when (nextRole) {
                            UserRole.Student -> Tab.Home
                            UserRole.Librarian -> Tab.Catalog
                            UserRole.Admin -> Tab.Dashboard
                        }
                    }
                )

                Box(Modifier.weight(1f)) {
                    when (currentUser.role) {
                        UserRole.Student -> {
                            when (tab) {
                                Tab.Home -> HomeScreen(
                                    mode = homeMode,
                                    query = query,
                                    onQueryChange = { query = it },
                                    category = category,
                                    onCategoryChange = { category = it },
                                    availabilityFilter = availability,
                                    onAvailabilityChange = { availability = it },
                                    savedIds = savedIds,
                                    allBooksList = booksList,
                                    categoriesList = LaisRepository.categories,
                                    onToggleSaved = { toggleInList(savedIds, it) },
                                    onBookClick = { selectedBookId = it.id },
                                    onTabSelected = { tab = it },
                                    onReset = {
                                        tab = Tab.Home
                                        homeMode = HomeMode.Smart
                                        query = ""
                                        category = "All"
                                        availability = "All"
                                    }
                                )

                                Tab.Saved -> SimpleScreen(
                                    icon = Icons.Filled.Bookmark,
                                    title = "Saved Books",
                                    description = "${savedIds.size} saved",
                                    savedBooksList = booksList.filter { it.id in savedIds },
                                    onBookClick = { selectedBookId = it.id }
                                )

                                Tab.History -> FeatureScreen(
                                    type = "history",
                                    allBooksList = booksList,
                                    historyList = historyList,
                                    onBookClick = { selectedBookId = it.id },
                                    alerts = alertIds,
                                    onToggleAlert = { toggleInList(alertIds, it) }
                                )

                                Tab.Returns -> FeatureScreen(
                                    type = "returns",
                                    allBooksList = booksList,
                                    historyList = historyList,
                                    onBookClick = { selectedBookId = it.id },
                                    alerts = alertIds,
                                    onToggleAlert = { toggleInList(alertIds, it) }
                                )

                                Tab.Alerts -> FeatureScreen(
                                    type = "alerts",
                                    allBooksList = booksList,
                                    historyList = historyList,
                                    onBookClick = { selectedBookId = it.id },
                                    alerts = alertIds,
                                    onToggleAlert = { toggleInList(alertIds, it) }
                                )

                                Tab.Account -> StudentAccountScreen(
                                    currentUser = currentUser,
                                    onUpdateUser = { updated ->
                                        currentUser = updated
                                        val idx = usersList.indexOfFirst { it.id == updated.id }
                                        if (idx != -1) usersList[idx] = updated
                                    },
                                    onSignOut = { flow = Flow.Login }
                                )

                                else -> HomeScreen(
                                    mode = HomeMode.Smart,
                                    query = "",
                                    onQueryChange = {},
                                    category = "All",
                                    onCategoryChange = {},
                                    availabilityFilter = "All",
                                    onAvailabilityChange = {},
                                    savedIds = savedIds,
                                    allBooksList = booksList,
                                    categoriesList = LaisRepository.categories,
                                    onToggleSaved = { toggleInList(savedIds, it) },
                                    onBookClick = { selectedBookId = it.id },
                                    onTabSelected = { tab = it },
                                    onReset = {}
                                )
                            }
                        }

                        UserRole.Librarian -> {
                            when (tab) {
                                Tab.Catalog -> LibrarianCatalogScreen(
                                    booksList = booksList,
                                    onAddBookClick = { showAddBookModal = true },
                                    onEditBookClick = { editingBook = it },
                                    onToggleOutdated = { bookId ->
                                        val idx = booksList.indexOfFirst { it.id == bookId }
                                        if (idx != -1) {
                                            val b = booksList[idx]
                                            booksList[idx] = b.copy(isOutdated = !b.isOutdated)
                                        }
                                    }
                                )

                                Tab.AddBook -> {
                                    showAddBookModal = true
                                    tab = Tab.Catalog
                                }

                                Tab.Returns -> LibrarianReturnsScreen(
                                    booksList = booksList,
                                    onUpdateReturnDate = { bookId, newDate ->
                                        val idx = booksList.indexOfFirst { it.id == bookId }
                                        if (idx != -1) {
                                            booksList[idx] = booksList[idx].copy(returnDate = newDate, status = Status.Borrowed)
                                        }
                                    },
                                    onMarkReturned = { bookId ->
                                        val idx = booksList.indexOfFirst { it.id == bookId }
                                        if (idx != -1) {
                                            val b = booksList[idx]
                                            booksList[idx] = b.copy(
                                                status = Status.Available,
                                                copies = (b.copies + 1).coerceAtMost(b.total),
                                                returnDate = null
                                            )
                                        }
                                    }
                                )

                                Tab.Account -> StudentAccountScreen(
                                    currentUser = currentUser,
                                    onUpdateUser = { updated ->
                                        currentUser = updated
                                        val idx = usersList.indexOfFirst { it.id == updated.id }
                                        if (idx != -1) usersList[idx] = updated
                                    },
                                    onSignOut = { flow = Flow.Login }
                                )

                                else -> LibrarianCatalogScreen(
                                    booksList = booksList,
                                    onAddBookClick = { showAddBookModal = true },
                                    onEditBookClick = { editingBook = it },
                                    onToggleOutdated = {}
                                )
                            }
                        }

                        UserRole.Admin -> {
                            when (tab) {
                                Tab.Dashboard -> AdminDashboardScreen(
                                    usersList = usersList,
                                    booksList = booksList,
                                    activitiesList = activitiesList,
                                    onNavigateTab = { tab = it }
                                )

                                Tab.Users -> AdminUserMgmtScreen(
                                    usersList = usersList,
                                    onAddUser = { newUser -> usersList.add(newUser) },
                                    onToggleActive = { userId ->
                                        val idx = usersList.indexOfFirst { it.id == userId }
                                        if (idx != -1) {
                                            val u = usersList[idx]
                                            usersList[idx] = u.copy(isActive = !u.isActive)
                                        }
                                    },
                                    onDeleteUser = { userId ->
                                        usersList.removeAll { it.id == userId }
                                    }
                                )

                                Tab.Audit -> AdminBookAuditScreen(booksList = booksList)

                                Tab.Logs -> AdminLogsScreen(logsList = logsList)

                                Tab.Backup -> AdminBackupScreen(
                                    backupsList = backupsList,
                                    onCreateBackup = {
                                        backupsList.add(
                                            0,
                                            BackupRecord(
                                                id = backupsList.size + 1,
                                                timestamp = "Just now",
                                                size = "4.3 MB",
                                                recordCount = booksList.size + usersList.size
                                            )
                                        )
                                    },
                                    onRestoreBackup = { bk ->
                                        logsList.add(
                                            0,
                                            SystemLog(
                                                id = logsList.size + 1,
                                                timestamp = "Just now",
                                                level = "ADMIN",
                                                message = "Restored system snapshot #${bk.id}",
                                                source = "BACKUP_MGR"
                                            )
                                        )
                                    }
                                )

                                Tab.Account -> StudentAccountScreen(
                                    currentUser = currentUser,
                                    onUpdateUser = { updated ->
                                        currentUser = updated
                                        val idx = usersList.indexOfFirst { it.id == updated.id }
                                        if (idx != -1) usersList[idx] = updated
                                    },
                                    onSignOut = { flow = Flow.Login }
                                )

                                else -> AdminDashboardScreen(
                                    usersList = usersList,
                                    booksList = booksList,
                                    activitiesList = activitiesList,
                                    onNavigateTab = { tab = it }
                                )
                            }
                        }
                    }
                }

                // Bottom Navigation Bar
                BottomNavBar(
                    userRole = currentUser.role,
                    currentTab = tab,
                    onTabSelected = { tab = it }
                )

                // Dialogs
                if (showAddBookModal) {
                    AddEditBookDialog(
                        bookToEdit = null,
                        onDismiss = { showAddBookModal = false },
                        onSaveBook = { newBook ->
                            val created = newBook.copy(id = (booksList.maxOfOrNull { it.id } ?: 0) + 1)
                            booksList.add(created)
                            showAddBookModal = false
                        }
                    )
                }

                if (editingBook != null) {
                    AddEditBookDialog(
                        bookToEdit = editingBook,
                        onDismiss = { editingBook = null },
                        onSaveBook = { updatedBook ->
                            val idx = booksList.indexOfFirst { it.id == updatedBook.id }
                            if (idx != -1) {
                                booksList[idx] = updatedBook
                            }
                            editingBook = null
                        }
                    )
                }
            }
        }
    }
}
