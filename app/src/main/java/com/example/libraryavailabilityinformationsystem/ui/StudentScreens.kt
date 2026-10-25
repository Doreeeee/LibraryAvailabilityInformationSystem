package com.example.libraryavailabilityinformationsystem.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.*

@Composable
fun HomeScreen(
    mode: HomeMode,
    query: String,
    onQueryChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    availabilityFilter: String,
    onAvailabilityChange: (String) -> Unit,
    savedIds: List<Int>,
    allBooksList: List<Book>,
    categoriesList: List<String>,
    onToggleSaved: (Int) -> Unit,
    onBookClick: (Book) -> Unit,
    onTabSelected: (Tab) -> Unit,
    onReset: () -> Unit
) {
    var filterOpen by rememberSaveable { mutableStateOf(false) }

    val filtered = remember(query, category, availabilityFilter, allBooksList) {
        val s = query.trim().lowercase()
        allBooksList.filter { b ->
            (s.isEmpty() || b.title.lowercase().contains(s) || b.author.lowercase().contains(s)) &&
            (category == "All" || b.category == category) &&
            (availabilityFilter == "All" || b.status.label == availabilityFilter)
        }
    }

    val showSmart = mode == HomeMode.Smart && query.isEmpty() && category == "All" && availabilityFilter == "All"

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 90.dp)) {
        if (mode == HomeMode.Smart) {
            item {
                val dark = Brush.horizontalGradient(listOf(Color(0xFF091D37), Color(0xFF103F70), Forest.copy(alpha = .8f)))
                Column(
                    Modifier
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(dark)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "TUESDAY, 20 MAY",
                            color = Color.White.copy(alpha = .67f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Filled.AutoAwesome, null, tint = Acid, modifier = Modifier.size(12.dp))
                        Text(" Smart home", color = Acid, fontSize = 10.sp)
                    }
                    Text(
                        "Your study shelf,",
                        color = Color.White,
                        fontFamily = Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text("in sync.", color = Acid, fontFamily = Serif, fontSize = 28.sp)
                }
            }
        }

        item {
            Row(
                Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("Title, author or keyword") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton({ onQueryChange("") }) { Icon(Icons.Filled.Close, "Clear search") }
                        }
                    }
                )
                FilledIconButton(
                    onClick = { filterOpen = !filterOpen },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (filterOpen) MintLight else Field,
                        contentColor = if (filterOpen) Forest else Ink
                    )
                ) {
                    BadgedBox(badge = { if (availabilityFilter != "All") Badge(containerColor = Coral) }) {
                        Icon(Icons.Filled.Tune, "Availability filters")
                    }
                }
            }
        }

        if (filterOpen) item {
            Column(
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 9.dp)
                    .fillMaxWidth()
                    .border(1.dp, Line, RoundedCornerShape(12.dp))
                    .padding(13.dp)
            ) {
                Text("AVAILABILITY FILTER", color = InkSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(9.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    items(listOf("All", "Available", "Borrowed", "Unavailable")) { opt ->
                        FilterChip(
                            selected = availabilityFilter == opt,
                            onClick = { onAvailabilityChange(opt); filterOpen = false },
                            label = { Text(opt) }
                        )
                    }
                }
            }
        }

        if (showSmart) {
            item {
                Column(Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AutoAwesome, null, tint = Forest, modifier = Modifier.size(15.dp))
                        Text(" Today at a glance", color = Forest, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Personalized for you", color = InkSoft, fontSize = 10.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val availableCount = allBooksList.count { it.status == Status.Available }
                        Snapshot(
                            Icons.Filled.Check,
                            Color(0xFFE9F7BD),
                            Color(0xFF526813),
                            "$availableCount ready",
                            "on shelf list",
                            Modifier.weight(1f)
                        ) { onAvailabilityChange("Available") }

                        Snapshot(
                            Icons.Filled.Bookmark,
                            Color(0xFFDEECFF),
                            Forest,
                            "${savedIds.size} saved",
                            "for next visit",
                            Modifier.weight(1f)
                        ) { onTabSelected(Tab.Saved) }

                        Snapshot(
                            Icons.Filled.Timer,
                            Color(0xFFFFE4DC),
                            Color(0xFFA94933),
                            "7 days",
                            "until next due",
                            Modifier.weight(1f)
                        ) { onTabSelected(Tab.Returns) }
                    }

                    if (allBooksList.isNotEmpty()) {
                        val featured = allBooksList.first()
                        Row(
                            Modifier
                                .padding(top = 14.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(19.dp))
                                .background(Acid)
                                .clickable { onBookClick(featured) }
                                .padding(start = 16.dp, top = 15.dp, bottom = 15.dp, end = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.School, null, modifier = Modifier.size(13.dp))
                                    Text(" PICKED FOR CS204", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                Text(
                                    featured.title,
                                    fontFamily = Serif,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Text(
                                    "96% match · ${featured.copies} copies available",
                                    color = Ink.copy(alpha = .66f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                                Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("View smart pick", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    Icon(Icons.Filled.ChevronRight, null, modifier = Modifier.size(14.dp))
                                }
                            }
                            BookCover(featured, Modifier.rotate(5f))
                        }
                    }
                }
            }
        }

        item {
            Column(Modifier.padding(top = 25.dp)) {
                Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.Bottom) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            when {
                                query.isNotEmpty() -> "LIBRARY SEARCH"
                                mode == HomeMode.Availability -> "CURRENT COLLECTION STATUS"
                                mode == HomeMode.Search -> "BROWSE BY CATEGORY"
                                else -> "CURATED FOR YOU"
                            },
                            color = Forest,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            when {
                                query.isNotEmpty() -> "Search results"
                                mode == HomeMode.Availability -> "All book statuses"
                                mode == HomeMode.Search -> "All books"
                                else -> "Discover next"
                            },
                            fontFamily = Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        )
                    }
                    Text("${filtered.size} found", color = InkSoft, fontSize = 12.sp)
                }
                LazyRow(
                    Modifier.padding(top = 13.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoriesList) { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { onCategoryChange(c) },
                            label = { Text(c) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Acid, selectedLabelColor = Ink)
                        )
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Column(Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MintLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Search, null, tint = Forest)
                    }
                    Text(
                        "No books found",
                        fontFamily = Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 21.sp,
                        modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                    )
                    Text("Try another title, author, or filter.", color = InkSoft, fontSize = 13.sp)
                    Button(onReset, Modifier.padding(top = 17.dp), shape = RoundedCornerShape(9.dp)) { Text("Clear search") }
                }
            }
        } else {
            items(filtered, key = { it.id }) { book ->
                BookRow(
                    book = book,
                    isSaved = book.id in savedIds,
                    showStatus = (mode == HomeMode.Availability),
                    onSave = { onToggleSaved(book.id) },
                    onOpen = { onBookClick(book) }
                )
            }
        }
    }
}

@Composable
fun BookRow(
    book: Book,
    isSaved: Boolean,
    showStatus: Boolean = false,
    onSave: () -> Unit,
    onOpen: () -> Unit
) {
    Column(Modifier.padding(horizontal = 20.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Box(
                Modifier
                    .size(82.dp, 110.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(MintLight)
                    .clickable(onClick = onOpen),
                contentAlignment = Alignment.Center
            ) {
                BookCover(book, Modifier.rotate(-3f))
            }
            Column(Modifier.weight(1f).heightIn(min = 110.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showStatus) {
                        StatusLabel(book.status, Modifier.weight(1f))
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    IconButton(onSave, Modifier.size(32.dp)) {
                        Icon(
                            if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            "Save book",
                            tint = if (isSaved) Forest else InkSoft
                        )
                    }
                }
                Column(Modifier.clickable(onClick = onOpen)) {
                    Text(
                        book.title,
                        fontFamily = Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        lineHeight = 19.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        book.author,
                        color = InkSoft,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                if (showStatus) {
                    BookNote(book)
                }
            }
        }
        HorizontalDivider(color = Line)
    }
}

@Composable
fun DetailScreen(
    book: Book,
    saved: Boolean,
    alert: Boolean,
    showStatusAndReturnDate: Boolean = false,
    onBack: () -> Unit,
    onToggleSaved: () -> Unit,
    onToggleAlert: () -> Unit
) {
    var reservedNotice by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().systemBarsPadding()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onBack) { Icon(Icons.Filled.ArrowBack, "Go back") }
            Text(
                "Book details",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            IconButton(onToggleSaved) {
                Icon(
                    if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    "Save book",
                    tint = if (saved) Forest else Ink
                )
            }
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(232.dp)
                    .background(Mint),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(215.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = .48f))
                )
                BookCover(book, Modifier.rotate(-4f), large = true)
            }

            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 30.dp)) {
                Text(book.category.uppercase(), color = Coral, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(
                    book.title,
                    fontFamily = Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    lineHeight = 30.sp,
                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                )
                Text("${book.author} · Year ${book.year}", color = InkSoft, fontSize = 13.sp)

                if (showStatusAndReturnDate) {
                    val (bg, fg, icon, head, sub) = when (book.status) {
                        Status.Available -> Card5(MintLight, Forest, Icons.Filled.Check, "Available now", "${book.copies} of ${book.total} copies available")
                        Status.Borrowed -> Card5(Color(0xFFFAEEE9), Color(0xFF92503F), Icons.Filled.Schedule, "Currently borrowed", "Expected back ${book.returnDate ?: "Soon"}")
                        Status.Unavailable -> Card5(Color(0xFFEDF1F5), Color(0xFF566676), Icons.Filled.Close, "Not currently available", "This title is not currently held by the school")
                    }

                    Row(
                        Modifier
                            .padding(top = 14.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(11.dp))
                            .background(bg)
                            .padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        Box(
                            Modifier
                                .size(37.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = .75f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = fg)
                        }
                        Column {
                            Text(head, color = fg, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(sub, color = fg, fontSize = 12.sp)
                        }
                    }

                    if (book.status == Status.Borrowed) {
                        HorizontalDivider(Modifier.padding(top = 16.dp), color = Line)
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Filled.CalendarMonth, null, tint = InkSoft)
                            Column {
                                Text("EXPECTED RETURN DATE", color = InkSoft, fontSize = 10.sp, letterSpacing = .6.sp)
                                Text(book.returnDate ?: "TBD", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Coral)
                            }
                        }
                        HorizontalDivider(color = Line)
                    }
                }

                if (reservedNotice) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Text(
                            "✓ Book availability notice set! We will notify your university email when this copy is ready.",
                            color = Color(0xFF2E7D32),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        if (showStatusAndReturnDate && book.status != Status.Available) {
            HorizontalDivider(color = Line)

            Surface(
                color = Paper,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            LaisRepository.requestBookNotification(
                                studentUid = "student_${System.currentTimeMillis() % 1000}",
                                bookId = book.id,
                                bookTitle = book.title
                            ) { _, _ ->
                                onToggleAlert()
                                reservedNotice = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(47.dp),
                        shape = RoundedCornerShape(11.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (alert) Color(0xFF39775F) else Forest)
                    ) {
                        Icon(if (alert) Icons.Filled.Check else Icons.Filled.NotificationsActive, null, Modifier.size(19.dp))
                        Text(if (alert) "  Notification set" else "  Notify me when available", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "We’ll send an electronic notification to your university email",
                        color = InkSoft,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
        }
    }
}

private data class Card5(val a: Color, val b: Color, val c: ImageVector, val d: String, val e: String)

@Composable
fun FeatureScreen(
    type: String,
    allBooksList: List<Book>,
    historyList: List<BorrowHistoryItem>,
    onBookClick: (Book) -> Unit,
    alerts: List<Int>,
    onToggleAlert: (Int) -> Unit
) {
    val (title, icon) = when (type) {
        "history" -> "Borrowing history" to Icons.Filled.History
        "returns" -> "Expected returns" to Icons.Filled.Event
        else -> "Book notifications" to Icons.Filled.NotificationsActive
    }

    val borrowedBooks = remember(allBooksList) { allBooksList.filter { it.status == Status.Borrowed } }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 90.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Forest),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Text(title, fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
        Spacer(Modifier.height(18.dp))

        if (type == "history") {
            historyList.forEach { item ->
                val matchedBook = allBooksList.find { it.id == item.bookId } ?: allBooksList.first()
                Row(
                    Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .border(1.dp, Line, RoundedCornerShape(18.dp))
                        .padding(13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        Modifier
                            .size(66.dp, 87.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MintLight)
                            .clickable { onBookClick(matchedBook) },
                        contentAlignment = Alignment.Center
                    ) {
                        BookCover(matchedBook)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            item.bookTitle,
                            fontFamily = Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            modifier = Modifier.clickable { onBookClick(matchedBook) }
                        )
                        Text(item.author, color = InkSoft, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(8.dp))
                        Pill("Returned · ${item.returnDate}", Icons.Filled.Check, Color(0xFFE7F7EE), Color(0xFF287452))
                    }
                }
            }
        } else {
            val list = if (type == "returns") borrowedBooks else allBooksList
            list.forEach { book ->
                Row(
                    Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .border(1.dp, Line, RoundedCornerShape(18.dp))
                        .padding(13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        Modifier
                            .size(66.dp, 87.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MintLight)
                            .clickable { onBookClick(book) },
                        contentAlignment = Alignment.Center
                    ) {
                        BookCover(book)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            book.title,
                            fontFamily = Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            modifier = Modifier.clickable { onBookClick(book) }
                        )
                        if (type == "returns") {
                            Text("● Currently borrowed", color = Color(0xFFA84C31), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Pill("Expected ${book.returnDate ?: "Soon"}", Icons.Filled.Event, Color(0xFFFFF0E9), Color(0xFFA84C31))
                        } else {
                            Text(book.author, color = InkSoft, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(6.dp))
                            StatusLabel(book.status)
                        }
                    }
                    if (type == "alerts") {
                        val on = book.id in alerts
                        FilledIconButton(
                            onClick = { onToggleAlert(book.id) },
                            modifier = Modifier.size(34.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (on) Forest else Paper,
                                contentColor = if (on) Color.White else InkSoft
                            )
                        ) {
                            Icon(if (on) Icons.Filled.Check else Icons.Filled.Notifications, "Toggle notification", Modifier.size(17.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleScreen(
    icon: ImageVector,
    title: String,
    description: String,
    savedBooksList: List<Book> = emptyList(),
    onBookClick: (Book) -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 25.dp, bottom = 90.dp)
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MintLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Forest)
        }
        Text(
            title,
            fontFamily = Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
        )
        Text(description, color = InkSoft, fontSize = 13.sp)

        Spacer(Modifier.height(20.dp))

        if (savedBooksList.isNotEmpty()) {
            savedBooksList.forEach { book ->
                BookRow(book, isSaved = true, onSave = {}, onOpen = { onBookClick(book) })
            }
        } else {
            Row(
                Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Cream)
                    .padding(17.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Icon(Icons.Filled.MenuBook, null, tint = InkSoft, modifier = Modifier.size(19.dp))
                Text("No saved books yet. Save books from search to prepare for your physical visit.", color = InkSoft, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StudentAccountScreen(
    currentUser: UserAccount,
    onUpdateUser: (UserAccount) -> Unit,
    onSignOut: () -> Unit
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var nameInput by rememberSaveable { mutableStateOf(currentUser.name) }
    var emailInput by rememberSaveable { mutableStateOf(currentUser.email) }
    var phoneInput by rememberSaveable { mutableStateOf(currentUser.phone ?: "") }
    var deptInput by rememberSaveable { mutableStateOf(currentUser.department ?: "") }
    var passInput by rememberSaveable { mutableStateOf(currentUser.password) }
    var successMsg by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 90.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MintLight)
        ) {
            Row(
                Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Acid),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        currentUser.name.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Ink
                    )
                }
                Column {
                    Text(currentUser.name, fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("${currentUser.role.label} · ID: ${currentUser.studentId ?: "N/A"}", color = Forest, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(currentUser.email, color = InkSoft, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        if (successMsg) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    "✓ Account details updated successfully!",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ACCOUNT INFORMATION", color = InkSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            TextButton({ isEditing = !isEditing }) {
                Text(if (isEditing) "Cancel" else "Edit Details", fontWeight = FontWeight.Bold)
            }
        }

        if (isEditing) {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Full Name") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email Address") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone Number") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = deptInput,
                onValueChange = { deptInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Department") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = passInput,
                onValueChange = { passInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = {
                    val updated = currentUser.copy(
                        name = nameInput,
                        email = emailInput,
                        phone = phoneInput,
                        department = deptInput,
                        password = passInput
                    )
                    LaisRepository.saveUserToFirestore(updated)
                    onUpdateUser(updated)
                    isEditing = false
                    successMsg = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Field)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailRow("Username", currentUser.username)
                    DetailRow("Department", currentUser.department ?: "Computer Science")
                    DetailRow("Phone", currentUser.phone ?: "Not provided")
                    DetailRow("Account Created", currentUser.createdAt)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Coral)
        ) {
            Icon(Icons.Filled.Logout, null, Modifier.size(18.dp))
            Text("  Sign Out of System", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = InkSoft, fontSize = 12.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Ink)
    }
}
