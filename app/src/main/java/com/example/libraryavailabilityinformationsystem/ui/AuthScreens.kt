package com.example.libraryavailabilityinformationsystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.*

@Composable
fun SplashScreen(onContinue: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A1D36), Color(0xFF0A2444), Forest)))
            .systemBarsPadding()
            .padding(horizontal = 27.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(20.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Box(
                Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Acid.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MenuBook,
                    contentDescription = null,
                    tint = Acid,
                    modifier = Modifier.size(38.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "LIBRARY AVAILABILITY",
                color = Color.White,
                fontFamily = Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 34.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "INFORMATION SYSTEM",
                color = Acid,
                fontFamily = Serif,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 2.5.sp
            )
        }

        Button(
            onClick = onContinue,
            modifier = Modifier
                .width(180.dp)
                .height(44.dp),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Acid, contentColor = Ink)
        ) {
            Text("Login", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Filled.ChevronRight, null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun LoginRegisterScreen(
    usersList: List<UserAccount>,
    onBack: () -> Unit,
    onAuthSuccess: (UserAccount) -> Unit,
    onRegisterUser: (UserAccount) -> Unit
) {
    var isSignUp by rememberSaveable { mutableStateOf(false) }

    // Sign In state
    var emailInput by rememberSaveable { mutableStateOf("") }
    var passwordInput by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(true) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // Sign Up state
    var regName by rememberSaveable { mutableStateOf("") }
    var regEmail by rememberSaveable { mutableStateOf("") }
    var regPassword by rememberSaveable { mutableStateOf("") }
    var regConfirmPassword by rememberSaveable { mutableStateOf("") }
    var regRole by rememberSaveable { mutableStateOf(UserRole.Student) }
    var regDept by rememberSaveable { mutableStateOf("Computer Science") }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 25.dp, end = 25.dp, top = 10.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onBack, Modifier.offset(x = (-8).dp)) {
                Icon(Icons.Filled.ArrowBack, "Back to splash screen")
            }
        }

        Spacer(Modifier.height(10.dp))

        Box(
            Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Forest.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.MenuBook,
                contentDescription = null,
                tint = Forest,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (!isSignUp) {
            // SIGN IN FLOW
            Text(
                "Sign In",
                color = Ink,
                fontFamily = Serif,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                placeholder = { Text("Enter your email") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Forest,
                    unfocusedBorderColor = Line,
                    focusedLabelColor = Forest,
                    unfocusedLabelColor = InkSoft,
                    focusedLeadingIconColor = Forest,
                    unfocusedLeadingIconColor = InkSoft
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrectEnabled = false),
                leadingIcon = { Icon(Icons.Filled.Email, null) }
            )
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                placeholder = { Text("Enter password") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Forest,
                    unfocusedBorderColor = Line,
                    focusedLabelColor = Forest,
                    unfocusedLabelColor = InkSoft,
                    focusedLeadingIconColor = Forest,
                    unfocusedLeadingIconColor = InkSoft
                ),
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                trailingIcon = {
                    IconButton({ showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            if (showPassword) "Hide password" else "Show password",
                            tint = InkSoft
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false)
            )

            if (errorMessage != null) {
                Text(
                    errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = Forest)
                    )
                    Text("Remember me", color = InkSoft, fontSize = 13.sp)
                }
                TextButton({}) { Text("Forgot password?", fontWeight = FontWeight.Bold, color = Forest, fontSize = 12.sp) }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    LaisRepository.authenticateWithFirebase(emailInput, passwordInput) { user, err ->
                        if (user != null) {
                            onAuthSuccess(user)
                        } else {
                            errorMessage = err ?: "Invalid credentials"
                        }
                    }
                },
                enabled = emailInput.isNotBlank() && passwordInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest, contentColor = Color.White)
            ) {
                Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Filled.ChevronRight, null)
            }

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = InkSoft, fontSize = 13.sp)
                TextButton(onClick = { isSignUp = true; errorMessage = null }) {
                    Text("Register Account", fontWeight = FontWeight.Bold, color = Forest, fontSize = 13.sp)
                }
            }
        } else {
            // REGISTER FLOW
            Text(
                "CREATE ACCOUNT",
                color = Forest,
                fontFamily = Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Role selection
            Text("I AM A:", color = InkSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(
                Modifier
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserRole.entries.forEach { role ->
                    FilterChip(
                        selected = regRole == role,
                        onClick = { regRole = role },
                        label = { Text(role.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Forest,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            OutlinedTextField(
                value = regName,
                onValueChange = { regName = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Full Name") },
                placeholder = { Text("e.g. John Doe") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Filled.Badge, null) }
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = regEmail,
                onValueChange = { regEmail = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                placeholder = { Text("e.g. doe@univ.edu") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrectEnabled = false),
                leadingIcon = { Icon(Icons.Filled.Email, null) }
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = regPassword,
                onValueChange = { regPassword = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                placeholder = { Text("Create password") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false)
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = regConfirmPassword,
                onValueChange = { regConfirmPassword = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Confirm Password") },
                placeholder = { Text("Re-enter password") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false)
            )

            if (errorMessage != null) {
                Text(
                    errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (regPassword != regConfirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    LaisRepository.registerStudentWithFirebase(
                        name = regName.ifBlank { "New Student" },
                        email = regEmail.trim(),
                        password = regPassword.trim(),
                        role = regRole
                    ) { registeredUser, err ->
                        if (registeredUser != null) {
                            onRegisterUser(registeredUser)
                            onAuthSuccess(registeredUser)
                        } else {
                            errorMessage = err ?: "Registration failed"
                        }
                    }
                },
                enabled = regName.isNotBlank() && regEmail.isNotBlank() && regPassword.isNotBlank() && regConfirmPassword.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest)
            ) {
                Text("Complete Registration", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Filled.Check, null)
            }

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = InkSoft, fontSize = 13.sp)
                TextButton(onClick = { isSignUp = false; errorMessage = null }) {
                    Text("Sign In", fontWeight = FontWeight.Bold, color = Forest, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    user: UserAccount,
    onBack: () -> Unit,
    onOpenTab: (Tab, String, HomeMode) -> Unit
) {
    val lastName = user.name.trim().split(" ").lastOrNull()?.uppercase() ?: "USER"

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF071930), Forest.copy(alpha = .8f))),
                    RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            IconButton(onBack, Modifier.offset(x = (-8).dp)) {
                Icon(Icons.Filled.ArrowBack, "Back to sign in", tint = Color.White)
            }
            Text(
                "WELCOME, $lastName",
                color = Acid,
                fontFamily = Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )
        }

        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 32.dp).navigationBarsPadding()) {
            when (user.role) {
                UserRole.Student -> {
                    FeatureItem(
                        Icons.Filled.Search,
                        "Search for books",
                        "Find any book by title, author or category.",
                        Color(0xFFE9F7BD),
                        Color(0xFF536B12)
                    ) { onOpenTab(Tab.Home, "All", HomeMode.Search) }

                    FeatureItem(
                        Icons.Filled.Event,
                        "Check expected return dates",
                        "See when borrowed books are due back.",
                        Color(0xFFFFE3DC),
                        Color(0xFFAE4934)
                    ) { onOpenTab(Tab.Returns, "All", HomeMode.Smart) }

                    FeatureItem(
                        Icons.Filled.History,
                        "Borrowing history",
                        "Review books you previously borrowed.",
                        Color(0xFFDFF4FC),
                        Color(0xFF27718B)
                    ) { onOpenTab(Tab.History, "All", HomeMode.Smart) }

                    FeatureItem(
                        Icons.Filled.NotificationsActive,
                        "Availability notifications",
                        "Manage alerts for unavailable titles.",
                        Color(0xFFFFF0C6),
                        Color(0xFF92691B)
                    ) { onOpenTab(Tab.Alerts, "All", HomeMode.Smart) }
                }

                UserRole.Librarian -> {
                    FeatureItem(
                        Icons.Filled.LibraryBooks,
                        "Manage Book Catalog",
                        "View, edit, or search all cataloged books.",
                        Color(0xFFDEECFF),
                        Forest
                    ) { onOpenTab(Tab.Catalog, "All", HomeMode.Smart) }

                    FeatureItem(
                        Icons.Filled.AddBox,
                        "Add New Book Record",
                        "Add new title, author, total copies & shelf location.",
                        Color(0xFFE9F7BD),
                        Color(0xFF536B12)
                    ) { onOpenTab(Tab.AddBook, "All", HomeMode.Smart) }

                    FeatureItem(
                        Icons.Filled.EditCalendar,
                        "Update Expected Return Dates",
                        "Set return dates & time for borrowed books.",
                        Color(0xFFFFE3DC),
                        Color(0xFFAE4934)
                    ) { onOpenTab(Tab.Returns, "All", HomeMode.Smart) }
                }

                UserRole.Admin -> {
                    FeatureItem(
                        Icons.Filled.Dashboard,
                        "System Overview",
                        "Monitor real-time system activity & metrics.",
                        Color(0xFFDEECFF),
                        Forest
                    ) { onOpenTab(Tab.Dashboard, "All", HomeMode.Smart) }

                    FeatureItem(
                        Icons.Filled.ManageAccounts,
                        "Manage User Accounts",
                        "Create, update, deactivate or delete student/librarian accounts.",
                        Color(0xFFFFF0C6),
                        Color(0xFF92691B)
                    ) { onOpenTab(Tab.Users, "All", HomeMode.Smart) }

                    FeatureItem(
                        Icons.Filled.ReceiptLong,
                        "View System Logs",
                        "Inspect detailed activity and audit logs.",
                        Color(0xFFDFF4FC),
                        Color(0xFF27718B)
                    ) { onOpenTab(Tab.Logs, "All", HomeMode.Smart) }

                    FeatureItem(
                        Icons.Filled.CloudSync,
                        "Backup & Restore Data",
                        "Create data backups and restore system snapshots.",
                        Color(0xFFFFE3DC),
                        Color(0xFFAE4934)
                    ) { onOpenTab(Tab.Backup, "All", HomeMode.Smart) }
                }
            }

            Text(
                "Select an option to continue",
                color = InkSoft,
                fontSize = 11.sp,
                modifier = Modifier
                    .padding(top = 15.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
