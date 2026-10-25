package com.example.libraryavailabilityinformationsystem.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.libraryavailabilityinformationsystem.models.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun LibrarianCatalogScreen(
    booksList: List<Book>,
    onAddBookClick: () -> Unit,
    onEditBookClick: (Book) -> Unit,
    onToggleOutdated: (Int) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var statusFilter by rememberSaveable { mutableStateOf("All") }

    val filtered = remember(searchQuery, statusFilter, booksList) {
        val q = searchQuery.trim().lowercase()
        booksList.filter { b ->
            (q.isEmpty() || b.title.lowercase().contains(q) || b.author.lowercase().contains(q)) &&
            (statusFilter == "All" || b.status.label == statusFilter)
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Manage Book Catalog", fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search catalog by title or author...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Filled.Search, null) }
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Available", "Borrowed", "Unavailable").forEach { st ->
                    FilterChip(
                        selected = statusFilter == st,
                        onClick = { statusFilter = st },
                        label = { Text(st, fontSize = 12.sp) }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        items(filtered, key = { it.id }) { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Paper),
                border = BorderStroke(1.dp, Line)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BookCover(book)
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusLabel(book.status, Modifier.weight(1f))
                                if (book.isOutdated) {
                                    Badge(containerColor = Coral) { Text("OUTDATED", fontSize = 9.sp, color = Color.White) }
                                }
                            }
                            Text(
                                book.title,
                                fontFamily = Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text("${book.author} (${book.year})", color = InkSoft, fontSize = 12.sp)
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Line)

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Copies: ${book.copies}/${book.total} available",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { onToggleOutdated(book.id) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(if (book.isOutdated) "Mark Current" else "Mark Outdated", fontSize = 10.sp)
                            }

                            Button(
                                onClick = { onEditBookClick(book) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MintLight, contentColor = Forest),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Filled.Edit, null, Modifier.size(12.dp))
                                Text(" Edit Info", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditBookDialog(
    bookToEdit: Book? = null,
    onDismiss: () -> Unit,
    onSaveBook: (Book) -> Unit
) {
    val context = LocalContext.current
    var title by rememberSaveable { mutableStateOf(bookToEdit?.title ?: "") }
    var author by rememberSaveable { mutableStateOf(bookToEdit?.author ?: "") }
    var yearStr by rememberSaveable { mutableStateOf((bookToEdit?.year ?: Calendar.getInstance().get(Calendar.YEAR)).toString()) }
    var category by rememberSaveable { mutableStateOf(bookToEdit?.category ?: "Computer Science") }
    var totalCopiesStr by rememberSaveable { mutableStateOf((bookToEdit?.total ?: 3).toString()) }
    var availableCopiesStr by rememberSaveable { mutableStateOf((bookToEdit?.copies ?: 3).toString()) }
    var status by rememberSaveable { mutableStateOf(bookToEdit?.status ?: Status.Available) }
    var returnDate by rememberSaveable { mutableStateOf(bookToEdit?.returnDate ?: "") }

    val openDatePicker = {
        val cal = Calendar.getInstance()
        val picker = DatePickerDialog(
            context,
            { _, y, m, d ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(y, m, d)
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                returnDate = sdf.format(selectedCal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        picker.datePicker.minDate = System.currentTimeMillis()
        picker.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (bookToEdit == null) "Add New Book Record" else "Edit Book Information", fontFamily = Serif, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author(s)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = yearStr,
                        onValueChange = { yearStr = it },
                        label = { Text("Year") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalCopiesStr,
                        onValueChange = { totalCopiesStr = it },
                        label = { Text("Total Copies") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = availableCopiesStr,
                        onValueChange = { availableCopiesStr = it },
                        label = { Text("Available Copies") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("STATUS & RETURN DATE", color = InkSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Status.entries.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st.label, fontSize = 11.sp) }
                        )
                    }
                }

                if (status == Status.Borrowed) {
                    OutlinedTextField(
                        value = returnDate,
                        onValueChange = { returnDate = it },
                        label = { Text("Expected Return Date") },
                        placeholder = { Text("Tap calendar to select date") },
                        singleLine = true,
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { openDatePicker() },
                        trailingIcon = {
                            IconButton(onClick = openDatePicker) {
                                Icon(Icons.Filled.CalendarMonth, "Select Date")
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tot = totalCopiesStr.toIntOrNull() ?: 1
                    val avail = availableCopiesStr.toIntOrNull() ?: 0
                    val yr = yearStr.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
                    val updatedBook = Book(
                        id = bookToEdit?.id ?: 0,
                        title = title.ifBlank { "Untitled Book" },
                        author = author.ifBlank { "Unknown Author" },
                        year = yr,
                        category = category.ifBlank { "General" },
                        status = status,
                        copies = avail,
                        total = tot,
                        location = "",
                        shelf = "",
                        returnDate = if (status == Status.Borrowed) returnDate.ifBlank {
                            val cal = Calendar.getInstance()
                            cal.add(Calendar.DAY_OF_MONTH, 7)
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)
                        } else null,
                        coverColors = bookToEdit?.coverColors ?: listOf(Color(0xFF0E3768), Color(0xFF081B31)),
                        isOutdated = bookToEdit?.isOutdated ?: false
                    )
                    onSaveBook(updatedBook)
                },
                enabled = title.isNotBlank() && author.isNotBlank()
            ) {
                Text("Save Book")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LibrarianReturnsScreen(
    booksList: List<Book>,
    onUpdateReturnDate: (Int, String) -> Unit,
    onMarkReturned: (Int) -> Unit
) {
    val context = LocalContext.current
    val borrowedList = remember(booksList) { booksList.filter { it.status == Status.Borrowed } }

    var selectedBookForDate by remember { mutableStateOf<Book?>(null) }
    var newDateInput by remember { mutableStateOf("") }

    val openDatePicker = {
        val cal = Calendar.getInstance()
        val picker = DatePickerDialog(
            context,
            { _, y, m, d ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(y, m, d)
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                newDateInput = sdf.format(selectedCal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        picker.datePicker.minDate = System.currentTimeMillis()
        picker.show()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("BORROWED BOOKS MANAGEMENT", color = Forest, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Text("Expected Return Dates", fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Update expected return dates when students extend or information changes.", color = InkSoft, fontSize = 12.sp)

        Spacer(Modifier.height(16.dp))

        if (borrowedList.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MintLight),
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) {
                Text("No books are currently marked as borrowed.", modifier = Modifier.padding(20.dp), fontSize = 13.sp)
            }
        } else {
            borrowedList.forEach { book ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Line),
                    colors = CardDefaults.cardColors(containerColor = Paper)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            BookCover(book)
                            Column(Modifier.weight(1f)) {
                                Text(book.title, fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(book.author, color = InkSoft, fontSize = 12.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Expected Due: ${book.returnDate ?: "Not Set"}", color = Coral, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = {
                                    selectedBookForDate = book
                                    newDateInput = book.returnDate ?: ""
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Filled.CalendarMonth, null, Modifier.size(14.dp))
                                Text(" Select Return Date", fontSize = 11.sp)
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { onMarkReturned(book.id) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF287452))
                            ) {
                                Icon(Icons.Filled.Check, null, Modifier.size(14.dp))
                                Text(" Mark Available", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedBookForDate != null) {
        AlertDialog(
            onDismissRequest = { selectedBookForDate = null },
            title = { Text("Select Expected Return Date") },
            text = {
                Column {
                    Text("Choose upcoming return date for '${selectedBookForDate?.title}':", fontSize = 12.sp, color = InkSoft)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDateInput,
                        onValueChange = { newDateInput = it },
                        label = { Text("Expected Return Date") },
                        placeholder = { Text("Tap calendar icon to pick date") },
                        singleLine = true,
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { openDatePicker() },
                        trailingIcon = {
                            IconButton(onClick = openDatePicker) {
                                Icon(Icons.Filled.CalendarMonth, "Select Date")
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateReturnDate(selectedBookForDate!!.id, newDateInput)
                    selectedBookForDate = null
                }) {
                    Text("Update Date")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBookForDate = null }) { Text("Cancel") }
            }
        )
    }
}
