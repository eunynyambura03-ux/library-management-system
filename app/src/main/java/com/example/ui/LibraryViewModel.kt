package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Book
import com.example.data.model.Genre
import com.example.data.model.Review
import com.example.data.model.Role
import com.example.data.model.User
import com.example.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    CATALOG,
    MANAGEMENT,
    SCHEMA_INSPECTOR
}

enum class SortOption(val label: String) {
    TITLE("Title A-Z"),
    RATING("Highest Rated"),
    YEAR("Newest First"),
    AVAILABILITY("Available First")
}

data class CatalogUiState(
    val books: List<Book> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val currentUser: User,
    val selectedGenreId: String? = null,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.TITLE,
    val borrowedBookIds: Set<String> = emptySet(),
    val totalCopiesInCirculation: Int = 0,
    val totalAvailableCopies: Int = 0
)

class LibraryViewModel(
    private val repository: LibraryRepository = LibraryRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(AppTab.CATALOG)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenreId = MutableStateFlow<String?>(null)
    val selectedGenreId: StateFlow<String?> = _selectedGenreId.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    private val _showAddBookDialog = MutableStateFlow(false)
    val showAddBookDialog: StateFlow<Boolean> = _showAddBookDialog.asStateFlow()

    private val _showReviewDialog = MutableStateFlow(false)
    val showReviewDialog: StateFlow<Boolean> = _showReviewDialog.asStateFlow()

    private val _showAddGenreDialog = MutableStateFlow(false)
    val showAddGenreDialog: StateFlow<Boolean> = _showAddGenreDialog.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    val users = repository.usersList

    val catalogUiState: StateFlow<CatalogUiState> = combine(
        repository.books,
        repository.genres,
        repository.reviews,
        repository.currentUser,
        _selectedGenreId,
        _searchQuery,
        _sortOption,
        repository.borrowedBookIds
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val books = args[0] as List<Book>
        @Suppress("UNCHECKED_CAST")
        val genres = args[1] as List<Genre>
        @Suppress("UNCHECKED_CAST")
        val reviews = args[2] as List<Review>
        val currentUser = args[3] as User
        val selectedGenre = args[4] as String?
        val query = args[5] as String
        val sort = args[6] as SortOption
        @Suppress("UNCHECKED_CAST")
        val borrowed = args[7] as Set<String>

        val filtered = books.filter { book ->
            val matchesGenre = selectedGenre == null || book.genreId == selectedGenre
            val matchesQuery = query.isBlank() ||
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true) ||
                    book.isbn.contains(query, ignoreCase = true)
            matchesGenre && matchesQuery
        }

        val sorted = when (sort) {
            SortOption.TITLE -> filtered.sortedBy { it.title.lowercase() }
            SortOption.YEAR -> filtered.sortedByDescending { it.publishedYear }
            SortOption.AVAILABILITY -> filtered.sortedWith(
                compareByDescending<Book> { it.copiesAvailable > 0 }
                    .thenByDescending { it.copiesAvailable }
            )
            SortOption.RATING -> {
                val ratingMap = reviews.groupBy { it.bookId }.mapValues { (_, revs) ->
                    revs.map { it.rating }.average()
                }
                filtered.sortedByDescending { ratingMap[it.id] ?: 0.0 }
            }
        }

        CatalogUiState(
            books = sorted,
            genres = genres,
            reviews = reviews,
            currentUser = currentUser,
            selectedGenreId = selectedGenre,
            searchQuery = query,
            sortOption = sort,
            borrowedBookIds = borrowed,
            totalCopiesInCirculation = books.sumOf { it.copiesTotal },
            totalAvailableCopies = books.sumOf { it.copiesAvailable }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState(currentUser = repository.currentUser.value)
    )

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedGenre(genreId: String?) {
        _selectedGenreId.value = genreId
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun selectBook(book: Book?) {
        _selectedBook.value = book
    }

    fun switchUser(userId: String) {
        repository.switchUser(userId)
        val user = repository.currentUser.value
        emitMessage("Switched user profile to ${user.name} (${user.role})")
    }

    fun borrowBook(bookId: String) {
        val result = repository.borrowBook(bookId)
        result.fold(
            onSuccess = { msg ->
                // Update selected book reference if open
                _selectedBook.value = repository.books.value.find { it.id == bookId }
                emitMessage(msg)
            },
            onFailure = { err ->
                emitMessage(err.message ?: "Failed to checkout book")
            }
        )
    }

    fun returnBook(bookId: String) {
        val result = repository.returnBook(bookId)
        result.fold(
            onSuccess = { msg ->
                _selectedBook.value = repository.books.value.find { it.id == bookId }
                emitMessage(msg)
            },
            onFailure = { err ->
                emitMessage(err.message ?: "Failed to return book")
            }
        )
    }

    fun addReview(bookId: String, rating: Int, comment: String) {
        val result = repository.addReview(bookId, rating, comment)
        result.fold(
            onSuccess = { msg ->
                _showReviewDialog.value = false
                emitMessage(msg)
            },
            onFailure = { err ->
                emitMessage(err.message ?: "Failed to submit review")
            }
        )
    }

    fun addBook(
        title: String,
        author: String,
        isbn: String,
        publishedYear: Int,
        copies: Int,
        genreId: String,
        summary: String
    ) {
        val result = repository.addBook(
            title = title,
            author = author,
            isbn = isbn,
            publishedYear = publishedYear,
            copies = copies,
            genreId = genreId,
            summary = summary
        )
        result.fold(
            onSuccess = { msg ->
                _showAddBookDialog.value = false
                emitMessage(msg)
            },
            onFailure = { err ->
                emitMessage(err.message ?: "Could not add book")
            }
        )
    }

    fun updateBookCopies(bookId: String, newTotal: Int) {
        val result = repository.updateBookCopies(bookId, newTotal)
        result.fold(
            onSuccess = { msg ->
                _selectedBook.value = repository.books.value.find { it.id == bookId }
                emitMessage(msg)
            },
            onFailure = { err ->
                emitMessage(err.message ?: "Could not update copies")
            }
        )
    }

    fun addGenre(name: String, description: String, colorHex: Long) {
        val result = repository.addGenre(name, description, colorHex)
        result.fold(
            onSuccess = { msg ->
                _showAddGenreDialog.value = false
                emitMessage(msg)
            },
            onFailure = { err ->
                emitMessage(err.message ?: "Could not create genre")
            }
        )
    }

    fun setAddBookDialogVisible(visible: Boolean) {
        _showAddBookDialog.value = visible
    }

    fun setReviewDialogVisible(visible: Boolean) {
        _showReviewDialog.value = visible
    }

    fun setAddGenreDialogVisible(visible: Boolean) {
        _showAddGenreDialog.value = visible
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _userMessage.emit(msg)
        }
    }
}
