package com.example.libraryavailabilityinformationsystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.libraryavailabilityinformationsystem.models.*



@Composable
fun AppBar(
    user: UserAccount,
    onBack: () -> Unit,
    onOpenAccount: () -> Unit,
    onSwitchRole: () -> Unit
) {
    Surface(
        color = Paper,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Ink)
            }
            Box(
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Forest),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MenuBook,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                Modifier
                    .padding(start = 9.dp)
                    .weight(1f)
            ) {
                Text(
                    "LAIS",
                    fontFamily = Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Ink
                )
            }

            BadgedBox(badge = { Badge(containerColor = Coral) }) {
                Icon(Icons.Filled.Notifications, "Notifications", tint = Ink)
            }
            Spacer(Modifier.width(12.dp))
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (user.role) {
                            UserRole.Student -> Acid
                            UserRole.Librarian -> Mint
                            UserRole.Admin -> Color(0xFFFFD1D1)
                        }
                    )
                    .clickable { onOpenAccount() },
                contentAlignment = Alignment.Center
            ) {
                val initials = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                Text(
                    if (initials.isNotEmpty()) initials else "U",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(
    userRole: UserRole,
    currentTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Paper,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = Ink,
            windowInsets = WindowInsets.navigationBars
        ) {
            when (userRole) {
                UserRole.Student -> {
                NavigationBarItem(
                    selected = currentTab == Tab.Home,
                    onClick = { onTabSelected(Tab.Home) },
                    icon = { Icon(Icons.Filled.Home, null) },
                    label = { Text("Home", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Saved,
                    onClick = { onTabSelected(Tab.Saved) },
                    icon = { Icon(Icons.Filled.Bookmark, null) },
                    label = { Text("Saved", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.History,
                    onClick = { onTabSelected(Tab.History) },
                    icon = { Icon(Icons.Filled.History, null) },
                    label = { Text("History", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Returns,
                    onClick = { onTabSelected(Tab.Returns) },
                    icon = { Icon(Icons.Filled.Event, null) },
                    label = { Text("Returns", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Account,
                    onClick = { onTabSelected(Tab.Account) },
                    icon = { Icon(Icons.Filled.Person, null) },
                    label = { Text("Profile", fontSize = 11.sp) }
                )
            }
            UserRole.Librarian -> {
                NavigationBarItem(
                    selected = currentTab == Tab.Catalog,
                    onClick = { onTabSelected(Tab.Catalog) },
                    icon = { Icon(Icons.Filled.LibraryBooks, null) },
                    label = { Text("Catalog", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.AddBook,
                    onClick = { onTabSelected(Tab.AddBook) },
                    icon = { Icon(Icons.Filled.AddBox, null) },
                    label = { Text("Add Book", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Returns,
                    onClick = { onTabSelected(Tab.Returns) },
                    icon = { Icon(Icons.Filled.EditCalendar, null) },
                    label = { Text("Return Dates", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Account,
                    onClick = { onTabSelected(Tab.Account) },
                    icon = { Icon(Icons.Filled.Badge, null) },
                    label = { Text("Profile", fontSize = 11.sp) }
                )
            }
            UserRole.Admin -> {
                NavigationBarItem(
                    selected = currentTab == Tab.Dashboard,
                    onClick = { onTabSelected(Tab.Dashboard) },
                    icon = { Icon(Icons.Filled.Dashboard, null) },
                    label = { Text("Overview", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Users,
                    onClick = { onTabSelected(Tab.Users) },
                    icon = { Icon(Icons.Filled.ManageAccounts, null) },
                    label = { Text("Users", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Audit,
                    onClick = { onTabSelected(Tab.Audit) },
                    icon = { Icon(Icons.Filled.MenuBook, null) },
                    label = { Text("Books Audit", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Logs,
                    onClick = { onTabSelected(Tab.Logs) },
                    icon = { Icon(Icons.Filled.ReceiptLong, null) },
                    label = { Text("Logs", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Backup,
                    onClick = { onTabSelected(Tab.Backup) },
                    icon = { Icon(Icons.Filled.CloudSync, null) },
                    label = { Text("Backup", fontSize = 11.sp) }
                )
            }
        }
    }
}
}

@Composable
fun BookCover(book: Book, modifier: Modifier = Modifier, large: Boolean = false) {
    val w = if (large) 115.dp else 59.dp
    val h = if (large) 164.dp else 88.dp
    Column(
        modifier
            .size(w, h)
            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 5.dp, bottomEnd = 5.dp, bottomStart = 2.dp))
            .background(Brush.verticalGradient(book.coverColors))
            .padding(start = if (large) 18.dp else 10.dp, top = if (large) 16.dp else 9.dp, end = 6.dp)
    ) {
        Text(
            book.category.uppercase(),
            color = Color.White,
            fontSize = if (large) 6.sp else 4.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            book.title,
            color = Color.White,
            fontFamily = Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = if (large) 15.sp else 8.sp,
            lineHeight = if (large) 16.sp else 9.sp,
            modifier = Modifier.padding(top = if (large) 14.dp else 7.dp),
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            book.author,
            color = Color.White,
            fontSize = if (large) 6.sp else 4.sp,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1
        )
    }
}

@Composable
fun StatusLabel(status: Status, modifier: Modifier = Modifier) {
    val (fg, dot) = when (status) {
        Status.Available -> Color(0xFF347C61) to Color(0xFF42A077)
        Status.Borrowed -> Color(0xFFA45B49) to Coral
        Status.Unavailable -> InkSoft to Color(0xFF8998A8)
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dot)
        )
        Text(
            " ${status.label.uppercase()}",
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = .5.sp
        )
    }
}

@Composable
fun BookNote(book: Book) {
    val (bg, fg, icon, text) = when (book.status) {
        Status.Available -> Quad(MintLight, Forest, Icons.Filled.Check, "${book.copies} of ${book.total} copies available")
        Status.Borrowed -> Quad(Color(0xFFFAEEE9), Color(0xFF965140), Icons.Filled.Schedule, "Expected ${book.returnDate ?: "Soon"}")
        Status.Unavailable -> Quad(Color(0xFFEDF1F5), Color(0xFF5F6D7C), Icons.Filled.Close, "Not currently held by the school")
    }
    Row(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(14.dp))
        Text(" $text", color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

private data class Quad(val a: Color, val b: Color, val c: ImageVector, val d: String)

@Composable
fun Snapshot(
    icon: ImageVector,
    bg: Color,
    fg: Color,
    title: String,
    sub: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 11.dp)
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(17.dp))
        }
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 9.dp))
        Text(sub, color = InkSoft, fontSize = 10.sp, lineHeight = 13.sp)
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    title: String,
    copy: String,
    bg: Color,
    fg: Color,
    onClick: () -> Unit
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp, horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = fg, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(copy, color = InkSoft, fontSize = 12.sp)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = Color(0xFF9AACBD))
        }
        HorizontalDivider(color = Line)
    }
}

@Composable
fun Pill(text: String, icon: ImageVector, bg: Color, fg: Color) {
    Row(
        Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(13.dp))
        Text(" $text", color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
