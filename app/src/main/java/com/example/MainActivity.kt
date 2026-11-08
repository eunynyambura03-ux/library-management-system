package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Role
import com.example.ui.AppTab
import com.example.ui.LibraryViewModel
import com.example.ui.components.AddBookDialog
import com.example.ui.components.AddGenreDialog
import com.example.ui.components.ReviewDialog
import com.example.ui.screens.BookDetailSheet
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.ManagementScreen
import com.example.ui.screens.SchemaInspectorScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LibraryApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryApp(viewModel: LibraryViewModel = viewModel()) {
    val state by viewModel.catalogUiState.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedBook by viewModel.selectedBook.collectAsStateWithLifecycle()
    val showAddBookDialog by viewModel.showAddBookDialog.collectAsStateWithLifecycle()
    val showReviewDialog by viewModel.showReviewDialog.collectAsStateWithLifecycle()
    val showAddGenreDialog by viewModel.showAddGenreDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var userMenuExpanded by remember { mutableStateOf(false) }

    // Collect snackbar notifications
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Handle back button when viewing detail sheet or on sub-tabs
    BackHandler(enabled = selectedBook != null || currentTab != AppTab.CATALOG) {
        if (selectedBook != null) {
            viewModel.selectBook(null)
        } else if (currentTab != AppTab.CATALOG) {
            viewModel.setTab(AppTab.CATALOG)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalLibrary,
                                contentDescription = "Logo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Library Hub",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Schema & Catalog System",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Role Profile Switcher Pill
                    Box {
                        Surface(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { userMenuExpanded = true }
                                .testTag("user_role_switch_pill"),
                            color = if (state.currentUser.role == Role.LIBRARIAN) {
                                Color(0xFFFEF3C7)
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (state.currentUser.role == Role.LIBRARIAN) {
                                        Icons.Default.AdminPanelSettings
                                    } else {
                                        Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    tint = if (state.currentUser.role == Role.LIBRARIAN) {
                                        Color(0xFFB45309)
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = state.currentUser.name.split(" ").first(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.currentUser.role == Role.LIBRARIAN) {
                                            Color(0xFF78350F)
                                        } else {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        }
                                    )
                                    Text(
                                        text = state.currentUser.role.name,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (state.currentUser.role == Role.LIBRARIAN) {
                                            Color(0xFFB45309)
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch profile",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = userMenuExpanded,
                            onDismissRequest = { userMenuExpanded = false }
                        ) {
                            Text(
                                text = " Switch Profile (Prisma User.role) ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            viewModel.users.forEach { user ->
                                val isSelected = user.id == state.currentUser.id
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (user.role == Role.LIBRARIAN) Color(0xFFD97706) else MaterialTheme.colorScheme.primary
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = user.avatarInitials,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = user.name + if (isSelected) " ✓" else "",
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "${user.role} • ${user.email}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.switchUser(user.id)
                                        userMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.CATALOG,
                    onClick = { viewModel.setTab(AppTab.CATALOG) },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Catalog") },
                    label = { Text("Catalog", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_catalog")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.MANAGEMENT,
                    onClick = { viewModel.setTab(AppTab.MANAGEMENT) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Manage") },
                    label = { Text("Manage", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_management")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.SCHEMA_INSPECTOR,
                    onClick = { viewModel.setTab(AppTab.SCHEMA_INSPECTOR) },
                    icon = { Icon(Icons.Default.AccountTree, contentDescription = "Schema") },
                    label = { Text("Schema & Code", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_schema")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.CATALOG -> {
                    CatalogScreen(
                        state = state,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onGenreSelect = { viewModel.setSelectedGenre(it) },
                        onSortSelect = { viewModel.setSortOption(it) },
                        onBookClick = { viewModel.selectBook(it) },
                        onBorrowClick = { viewModel.borrowBook(it) },
                        onReturnClick = { viewModel.returnBook(it) },
                        onOpenAddBookDialog = { viewModel.setAddBookDialogVisible(true) }
                    )
                }

                AppTab.MANAGEMENT -> {
                    ManagementScreen(
                        state = state,
                        allUsers = viewModel.users,
                        onOpenAddBookDialog = { viewModel.setAddBookDialogVisible(true) },
                        onOpenAddGenreDialog = { viewModel.setAddGenreDialogVisible(true) },
                        onUpdateCopies = { bookId, newTotal -> viewModel.updateBookCopies(bookId, newTotal) },
                        onSwitchUser = { userId -> viewModel.switchUser(userId) }
                    )
                }

                AppTab.SCHEMA_INSPECTOR -> {
                    SchemaInspectorScreen(
                        onCopyMessage = { msg ->
                            // Feedback via snackbar
                            viewModel.setTab(AppTab.SCHEMA_INSPECTOR)
                        }
                    )
                }
            }
        }
    }

    // Book Detail Sheet Modal
    selectedBook?.let { book ->
        val genre = state.genres.find { it.id == book.genreId }
        val reviews = state.reviews.filter { it.bookId == book.id }
        val isBorrowed = state.borrowedBookIds.contains(book.id)

        BookDetailSheet(
            book = book,
            genre = genre,
            reviews = reviews,
            currentUser = state.currentUser,
            isBorrowedByMe = isBorrowed,
            onDismiss = { viewModel.selectBook(null) },
            onBorrowClick = { viewModel.borrowBook(book.id) },
            onReturnClick = { viewModel.returnBook(book.id) },
            onOpenReviewDialog = { viewModel.setReviewDialogVisible(true) },
            onUpdateCopies = { newTotal -> viewModel.updateBookCopies(book.id, newTotal) }
        )
    }

    // Review Dialog (Stretch task)
    if (showReviewDialog && selectedBook != null) {
        ReviewDialog(
            book = selectedBook!!,
            currentUser = state.currentUser,
            onDismiss = { viewModel.setReviewDialogVisible(false) },
            onSubmit = { rating, comment ->
                viewModel.addReview(selectedBook!!.id, rating, comment)
            }
        )
    }

    // Add Book Dialog (Librarian)
    if (showAddBookDialog) {
        AddBookDialog(
            genres = state.genres,
            onDismiss = { viewModel.setAddBookDialogVisible(false) },
            onConfirm = { title, author, isbn, year, copies, genreId, summary ->
                viewModel.addBook(title, author, isbn, year, copies, genreId, summary)
            }
        )
    }

    // Add Genre Dialog (Librarian)
    if (showAddGenreDialog) {
        AddGenreDialog(
            onDismiss = { viewModel.setAddGenreDialogVisible(false) },
            onConfirm = { name, desc, color ->
                viewModel.addGenre(name, desc, color)
            }
        )
    }
}
