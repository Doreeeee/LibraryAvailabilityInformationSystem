package com.example.libraryavailabilityinformationsystem.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.*

@Composable
fun AdminDashboardScreen(
    usersList: List<UserAccount>,
    booksList: List<Book>,
    activitiesList: List<SystemActivity>,
    onNavigateTab: (Tab) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
    ) {
        Text("System Overview", fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Real-time metrics and system activity monitoring.", color = InkSoft, fontSize = 12.sp)

        Spacer(Modifier.height(16.dp))

        // Metric cards
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Users", "${usersList.size}", "Students & Staff", Icons.Filled.Group, Forest, Modifier.weight(1f)) { onNavigateTab(Tab.Users) }
            MetricCard("Total Books", "${booksList.size}", "Catalog Titles", Icons.Filled.MenuBook, Color(0xFF287452), Modifier.weight(1f)) { onNavigateTab(Tab.Audit) }
            MetricCard("Borrowed", "${booksList.count { it.status == Status.Borrowed }}", "Out on Loan", Icons.Filled.Schedule, Coral, Modifier.weight(1f)) { onNavigateTab(Tab.Audit) }
        }

        Spacer(Modifier.height(20.dp))

        // Quick Actions
        Text("SYSTEM MANAGEMENT", color = InkSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onNavigateTab(Tab.Users) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest)
            ) {
                Icon(Icons.Filled.PersonAdd, null, Modifier.size(16.dp))
                Text(" Manage Users", fontSize = 11.sp)
            }
            Button(
                onClick = { onNavigateTab(Tab.Backup) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintLight, contentColor = Ink)
            ) {
                Icon(Icons.Filled.CloudSync, null, Modifier.size(16.dp))
                Text(" Backup Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("RECENT SYSTEM ACTIVITY", color = InkSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        activitiesList.forEach { act ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Paper),
                border = BorderStroke(1.dp, Line)
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        Modifier.size(36.dp).clip(CircleShape).background(MintLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            when (act.type) {
                                "SEARCH" -> Icons.Filled.Search
                                "LOGIN" -> Icons.Filled.Lock
                                "RESERVATION" -> Icons.Filled.Notifications
                                else -> Icons.Filled.Settings
                            },
                            null,
                            tint = Forest,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(act.action, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${act.user} · ${act.timestamp}", color = InkSoft, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, sub: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Paper),
        border = BorderStroke(1.dp, Line)
    ) {
        Column(Modifier.padding(12.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.padding(top = 4.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Ink)
            Text(sub, color = InkSoft, fontSize = 10.sp)
        }
    }
}

@Composable
fun AdminUserMgmtScreen(
    usersList: List<UserAccount>,
    onAddUser: (UserAccount) -> Unit,
    onToggleActive: (Int) -> Unit,
    onDeleteUser: (Int) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var roleFilter by rememberSaveable { mutableStateOf("All") }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    val filteredUsers = remember(searchQuery, roleFilter, usersList) {
        val q = searchQuery.trim().lowercase()
        usersList.filter { u ->
            (q.isEmpty() || u.name.lowercase().contains(q) || u.email.lowercase().contains(q) || u.username.lowercase().contains(q)) &&
            (roleFilter == "All" || u.role.label == roleFilter)
        }
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("USER MANAGEMENT", color = Coral, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Text("Accounts Control", fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest)
                ) {
                    Icon(Icons.Filled.PersonAdd, null, Modifier.size(16.dp))
                    Text(" Add User", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search users by name, email, or username...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Filled.Search, null) }
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Student", "Librarian Staff", "Administrator").forEach { r ->
                    FilterChip(
                        selected = roleFilter == r,
                        onClick = { roleFilter = r },
                        label = { Text(r, fontSize = 11.sp) }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        items(filteredUsers, key = { it.id }) { u ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                colors = CardDefaults.cardColors(containerColor = Paper)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            Modifier.size(42.dp).clip(CircleShape).background(if (u.isActive) MintLight else Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(u.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = if (u.isActive) Forest else Color.Red)
                        }
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(u.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(Modifier.width(6.dp))
                                Badge(containerColor = if (u.isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)) {
                                    Text(if (u.isActive) "ACTIVE" else "DEACTIVATED", fontSize = 9.sp, color = if (u.isActive) Color(0xFF2E7D32) else Color.Red)
                                }
                            }
                            Text("${u.role.label} · Username: ${u.username}", fontSize = 12.sp, color = Forest, fontWeight = FontWeight.SemiBold)
                            Text(u.email, fontSize = 11.sp, color = InkSoft)
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Line)

                    Row(
                        Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Created: ${u.createdAt}", fontSize = 10.sp, color = InkSoft)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    onToggleActive(u.id)
                                    LaisRepository.logActivity(
                                        performedBy = "Administrator",
                                        performedByRole = "admin",
                                        action = if (u.isActive) "DEACTIVATE_USER" else "ACTIVATE_USER",
                                        targetUserId = u.id.toString(),
                                        description = "Admin ${if (u.isActive) "deactivated" else "activated"} user account '${u.name}'"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(if (u.isActive) "Deactivate" else "Activate", fontSize = 10.sp)
                            }

                            IconButton(
                                onClick = {
                                    onDeleteUser(u.id)
                                    LaisRepository.deleteUserFromFirestore(u.id)
                                    LaisRepository.logActivity(
                                        performedBy = "Administrator",
                                        performedByRole = "admin",
                                        action = "DELETE_USER",
                                        targetUserId = u.id.toString(),
                                        description = "Admin deleted user account '${u.name}'"
                                    )
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Filled.Delete, "Delete user", tint = Coral, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newEmail by remember { mutableStateOf("") }
        var newUsername by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("123") }
        var newRole by remember { mutableStateOf(UserRole.Student) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create New User Account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        UserRole.entries.forEach { role ->
                            FilterChip(
                                selected = newRole == role,
                                onClick = { newRole = role },
                                label = { Text(role.label, fontSize = 11.sp) }
                            )
                        }
                    }
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name") }, singleLine = true)
                    OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") }, singleLine = true)
                    OutlinedTextField(value = newUsername, onValueChange = { newUsername = it }, label = { Text("Username") }, singleLine = true)
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("Password") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newUser = UserAccount(
                            id = (usersList.maxOfOrNull { it.id } ?: 0) + 1,
                            name = newName.ifBlank { "User" },
                            email = newEmail.ifBlank { "${newUsername}@univ.edu" },
                            username = newUsername.ifBlank { "user" },
                            password = newPass,
                            role = newRole,
                            studentId = if (newRole == UserRole.Student) "STU-2025-099" else null
                        )
                        onAddUser(newUser)
                        LaisRepository.saveUserToFirestore(newUser)
                        LaisRepository.logActivity(
                            performedBy = "Administrator",
                            performedByRole = "admin",
                            action = "CREATE_USER",
                            targetUserId = newUser.id.toString(),
                            description = "Admin created account for ${newUser.name} (${newUser.role.label})"
                        )
                        showAddDialog = false
                    },
                    enabled = newName.isNotBlank() && newUsername.isNotBlank()
                ) {
                    Text("Create Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminBookAuditScreen(booksList: List<Book>) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("SYSTEM CATALOG AUDIT", color = Coral, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Text("Books & Statuses Overview", fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Real-time view of all library book records across campus.", color = InkSoft, fontSize = 12.sp)

        Spacer(Modifier.height(16.dp))

        booksList.forEach { book ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Line),
                colors = CardDefaults.cardColors(containerColor = Paper)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BookCover(book)
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusLabel(book.status, Modifier.weight(1f))
                            Text("ID #${book.id}", fontSize = 10.sp, color = InkSoft, fontWeight = FontWeight.Bold)
                        }
                        Text(book.title, fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${book.author} · Copies: ${book.copies}/${book.total}", fontSize = 12.sp, color = InkSoft)
                        Text("Location: ${book.location}", fontSize = 11.sp, color = Forest)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminLogsScreen(logsList: List<SystemLog>) {
    var levelFilter by rememberSaveable { mutableStateOf("All") }

    val filteredLogs = remember(levelFilter, logsList) {
        if (levelFilter == "All") logsList else logsList.filter { it.level == levelFilter }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("SECURITY & AUDIT TRAIL", color = Coral, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Text("System Logs Viewer", fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Electronic log records of all system actions and events.", color = InkSoft, fontSize = 12.sp)

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "INFO", "ADMIN", "WARN").forEach { lvl ->
                FilterChip(
                    selected = levelFilter == lvl,
                    onClick = { levelFilter = lvl },
                    label = { Text(lvl, fontSize = 11.sp) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        filteredLogs.forEach { log ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Line),
                colors = CardDefaults.cardColors(containerColor = Paper)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Badge(
                            containerColor = when (log.level) {
                                "ADMIN" -> Forest
                                "WARN" -> Coral
                                else -> Mint
                            }
                        ) {
                            Text(log.level, fontSize = 9.sp, color = if (log.level == "INFO") Ink else Color.White)
                        }
                        Text(log.timestamp, fontSize = 10.sp, color = InkSoft)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(log.message, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Ink)
                    Text("Source: ${log.source}", fontSize = 10.sp, color = InkSoft, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
fun AdminBackupScreen(
    backupsList: List<BackupRecord>,
    onCreateBackup: () -> Unit,
    onRestoreBackup: (BackupRecord) -> Unit
) {
    var statusNotice by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("DATA PROTECTION", color = Coral, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Text("Backup & Restore System Data", fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Create data backups or restore system state after failures.", color = InkSoft, fontSize = 12.sp)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onCreateBackup()
                statusNotice = "New system backup snapshot generated successfully!"
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest)
        ) {
            Icon(Icons.Filled.CloudUpload, null, Modifier.size(18.dp))
            Text("  Create System Backup Snapshot", fontWeight = FontWeight.Bold)
        }

        if (statusNotice != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text(statusNotice!!, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("BACKUP HISTORY", color = InkSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        backupsList.forEach { bk ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Line),
                colors = CardDefaults.cardColors(containerColor = Paper)
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Snapshot #${bk.id} (${bk.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${bk.recordCount} records · ${bk.timestamp}", fontSize = 11.sp, color = InkSoft)
                    }

                    OutlinedButton(
                        onClick = {
                            onRestoreBackup(bk)
                            statusNotice = "System state restored from snapshot #${bk.id} (${bk.timestamp})"
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.CloudDownload, null, Modifier.size(14.dp))
                        Text(" Restore", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
