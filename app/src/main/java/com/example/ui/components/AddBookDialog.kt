package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Genre

@Composable
fun AddBookDialog(
    genres: List<Genre>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        author: String,
        isbn: String,
        publishedYear: Int,
        copies: Int,
        genreId: String,
        summary: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var publishedYearText by remember { mutableStateOf("2024") }
    var copiesText by remember { mutableStateOf("3") }
    var summary by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf(genres.firstOrNull()) }
    var genreDropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Add Book to Catalog",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Librarian Role • Creates Book with genreId foreign key",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_book_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_book_author_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Genre Dropdown Selector (Foreign key genreId)
                Text(
                    text = "Genre (Foreign Key: genreId) *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedGenre?.name ?: "Select Genre",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { genreDropdownExpanded = true }
                            .testTag("add_book_genre_dropdown"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    DropdownMenu(
                        expanded = genreDropdownExpanded,
                        onDismissRequest = { genreDropdownExpanded = false }
                    ) {
                        genres.forEach { genre ->
                            DropdownMenuItem(
                                text = { Text(genre.name) },
                                onClick = {
                                    selectedGenre = genre
                                    genreDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN (Unique) *") },
                    placeholder = { Text("e.g. 978-0123456789") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_book_isbn_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = publishedYearText,
                        onValueChange = { publishedYearText = it },
                        label = { Text("Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_book_year_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = copiesText,
                        onValueChange = { copiesText = it },
                        label = { Text("Copies") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_book_copies_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Synopsis / Summary") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_book_summary_input"),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val year = publishedYearText.toIntOrNull() ?: 2024
                    val copies = copiesText.toIntOrNull() ?: 1
                    val gId = selectedGenre?.id ?: genres.firstOrNull()?.id ?: ""
                    onConfirm(title, author, isbn, year, copies, gId, summary)
                },
                enabled = title.isNotBlank() && author.isNotBlank() && isbn.isNotBlank() && selectedGenre != null,
                modifier = Modifier.testTag("confirm_add_book_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add Book")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_add_book_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
