package com.example.data.repository

import com.example.data.model.Book
import com.example.data.model.Genre
import com.example.data.model.Review
import com.example.data.model.Role
import com.example.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class LibraryRepository {

    // Pre-seeded Users (1 Librarian, 2 Patrons)
    val usersList = listOf(
        User(
            id = "user_librarian_1",
            name = "Eleanor Vance",
            email = "librarian@libraryhub.org",
            role = Role.LIBRARIAN
        ),
        User(
            id = "user_patron_1",
            name = "Marcus Aurelius",
            email = "marcus.aurelius@reader.org",
            role = Role.PATRON
        ),
        User(
            id = "user_patron_2",
            name = "Ada Lovelace",
            email = "ada.lovelace@reader.org",
            role = Role.PATRON
        )
    )

    private val _currentUser = MutableStateFlow(usersList[1]) // Start as Patron Marcus
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // 5 Seeded Genres
    private val initialGenres = listOf(
        Genre(
            id = "genre_scitech",
            name = "Science & Technology",
            description = "Foundations of computer science, engineering, physics, and computing architecture.",
            colorHex = 0xFF0284C7 // Sky Blue
        ),
        Genre(
            id = "genre_philosophy",
            name = "Philosophy & Ethics",
            description = "Classical logic, moral philosophy, epistemological inquiries, and mindful living.",
            colorHex = 0xFFD97706 // Warm Amber
        ),
        Genre(
            id = "genre_litfiction",
            name = "Literary Fiction",
            description = "Acclaimed contemporary and classic prose exploring the human condition.",
            colorHex = 0xFF7C3AED // Deep Violet
        ),
        Genre(
            id = "genre_mystery",
            name = "Mystery & Thriller",
            description = "Suspenseful detective stories, forensic mysteries, and psychological puzzles.",
            colorHex = 0xFFDC2626 // Crimson
        ),
        Genre(
            id = "genre_scifi",
            name = "Science Fiction & Fantasy",
            description = "Speculative worlds, cosmic exploration, epic sagas, and cybernetic futures.",
            colorHex = 0xFF059669 // Emerald
        )
    )

    private val _genres = MutableStateFlow(initialGenres)
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    // 16 Seeded Books distributed across 5 Genres
    private val initialBooks = listOf(
        // Science & Technology (4 books)
        Book(
            id = "book_1",
            title = "Designing Data-Intensive Applications",
            author = "Martin Kleppmann",
            isbn = "978-1449373320",
            publishedYear = 2017,
            copiesTotal = 4,
            copiesAvailable = 3,
            genreId = "genre_scitech",
            summary = "The definitive guide to distributed databases, storage engines, stream processing, and data consistency models."
        ),
        Book(
            id = "book_2",
            title = "Structure and Interpretation of Computer Programs",
            author = "Harold Abelson & Gerald Jay Sussman",
            isbn = "978-0262510875",
            publishedYear = 1996,
            copiesTotal = 3,
            copiesAvailable = 2,
            genreId = "genre_scitech",
            summary = "The classic MIT computer science text emphasizing computational abstraction and functional programming in Scheme."
        ),
        Book(
            id = "book_3",
            title = "Clean Code",
            author = "Robert C. Martin",
            isbn = "978-0132350884",
            publishedYear = 2008,
            copiesTotal = 5,
            copiesAvailable = 5,
            genreId = "genre_scitech",
            summary = "A handbook of agile software craftsmanship covering meaningful naming, unit testing, refactoring, and code smells."
        ),
        Book(
            id = "book_4",
            title = "The Pragmatic Programmer",
            author = "David Thomas & Andrew Hunt",
            isbn = "978-0135957059",
            publishedYear = 2019,
            copiesTotal = 4,
            copiesAvailable = 4,
            genreId = "genre_scitech",
            summary = "Practical techniques for career software engineers, from orthogonality and debugging to decoupling and estimation."
        ),

        // Philosophy & Ethics (3 books)
        Book(
            id = "book_5",
            title = "Meditations",
            author = "Marcus Aurelius",
            isbn = "978-0140449334",
            publishedYear = 2006,
            copiesTotal = 6,
            copiesAvailable = 4,
            genreId = "genre_philosophy",
            summary = "Personal spiritual reflections and journal entries on Stoic philosophy by the Roman Emperor Marcus Aurelius."
        ),
        Book(
            id = "book_6",
            title = "Nicomachean Ethics",
            author = "Aristotle",
            isbn = "978-0199213610",
            publishedYear = 2009,
            copiesTotal = 3,
            copiesAvailable = 3,
            genreId = "genre_philosophy",
            summary = "Inquiry into the ultimate nature of human happiness (eudaimonia) and the development of moral virtue."
        ),
        Book(
            id = "book_7",
            title = "Justice: What is the Right Thing to Do?",
            author = "Michael J. Sandel",
            isbn = "978-0374532505",
            publishedYear = 2010,
            copiesTotal = 3,
            copiesAvailable = 2,
            genreId = "genre_philosophy",
            summary = "Harvard political philosopher Michael Sandel explores utilitarianism, libertarianism, Kantian duty, and Aristotle."
        ),

        // Literary Fiction (3 books)
        Book(
            id = "book_8",
            title = "One Hundred Years of Solitude",
            author = "Gabriel García Márquez",
            isbn = "978-0060883287",
            publishedYear = 1967,
            copiesTotal = 4,
            copiesAvailable = 3,
            genreId = "genre_litfiction",
            summary = "The multi-generational saga of the Buendía family in the mythical town of Macondo, blending magical realism and history."
        ),
        Book(
            id = "book_9",
            title = "The Remains of the Day",
            author = "Kazuo Ishiguro",
            isbn = "978-0679731726",
            publishedYear = 1989,
            copiesTotal = 3,
            copiesAvailable = 3,
            genreId = "genre_litfiction",
            summary = "The poignant journey of an aging English butler reflecting on devotion, repressed feelings, and geopolitical illusions."
        ),
        Book(
            id = "book_10",
            title = "Crime and Punishment",
            author = "Fyodor Dostoevsky",
            isbn = "978-0140449136",
            publishedYear = 1866,
            copiesTotal = 4,
            copiesAvailable = 2,
            genreId = "genre_litfiction",
            summary = "Psychological drama of impoverished student Raskolnikov who murders a pawnbroker to test his theory of extraordinary men."
        ),

        // Mystery & Thriller (3 books)
        Book(
            id = "book_11",
            title = "The Hound of the Baskervilles",
            author = "Arthur Conan Doyle",
            isbn = "978-0141439242",
            publishedYear = 1902,
            copiesTotal = 4,
            copiesAvailable = 4,
            genreId = "genre_mystery",
            summary = "Sherlock Holmes and Dr. Watson investigate the curse of an spectral beast on Dartmoor hunting the Baskerville heirs."
        ),
        Book(
            id = "book_12",
            title = "And Then There Were None",
            author = "Agatha Christie",
            isbn = "978-0062073488",
            publishedYear = 1939,
            copiesTotal = 5,
            copiesAvailable = 3,
            genreId = "genre_mystery",
            summary = "Ten strangers are lured to an isolated mansion on an island, where each is accused of a past crime and picked off one by one."
        ),
        Book(
            id = "book_13",
            title = "The Girl with the Dragon Tattoo",
            author = "Stieg Larsson",
            isbn = "978-0307949486",
            publishedYear = 2005,
            copiesTotal = 3,
            copiesAvailable = 2,
            genreId = "genre_mystery",
            summary = "Investigative journalist Mikael Blomkvist and hacker Lisbeth Salander uncover dark secrets in a wealthy Swedish dynasty."
        ),

        // Science Fiction & Fantasy (3 books)
        Book(
            id = "book_14",
            title = "Dune",
            author = "Frank Herbert",
            isbn = "978-0441172719",
            publishedYear = 1965,
            copiesTotal = 6,
            copiesAvailable = 5,
            genreId = "genre_scifi",
            summary = "The grand epic of Paul Atreides on desert planet Arrakis, guardian of the universe's most precious spice melange."
        ),
        Book(
            id = "book_15",
            title = "Neuromancer",
            author = "William Gibson",
            isbn = "978-0441569595",
            publishedYear = 1984,
            copiesTotal = 4,
            copiesAvailable = 4,
            genreId = "genre_scifi",
            summary = "The seminal cyberpunk novel following console cowboy Case as he pulls off a matrix cyberspace heist against powerful AI."
        ),
        Book(
            id = "book_16",
            title = "Snow Crash",
            author = "Neal Stephenson",
            isbn = "978-0553380958",
            publishedYear = 1992,
            copiesTotal = 3,
            copiesAvailable = 3,
            genreId = "genre_scifi",
            summary = "Hiro Protagonist, pizza delivery driver and hacker, investigates a virtual neuro-linguistic narcotic threatening the Metaverse."
        )
    )

    private val _books = MutableStateFlow(initialBooks)
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    // Stretch Goal: Reviews relation
    private val initialReviews = listOf(
        Review(
            id = "rev_1",
            bookId = "book_1",
            userId = "user_patron_2",
            userName = "Ada Lovelace",
            userRole = Role.PATRON,
            rating = 5,
            comment = "The definitive masterclass on distributed systems, replication lag, and storage architectures!",
            dateText = "2 days ago"
        ),
        Review(
            id = "rev_2",
            bookId = "book_5",
            userId = "user_patron_1",
            userName = "Marcus Aurelius",
            userRole = Role.PATRON,
            rating = 5,
            comment = "Personal reflections on steadfast reason, inner Citadel, and acceptance of the cosmos.",
            dateText = "1 week ago"
        ),
        Review(
            id = "rev_3",
            bookId = "book_14",
            userId = "user_patron_2",
            userName = "Ada Lovelace",
            userRole = Role.PATRON,
            rating = 5,
            comment = "Immense ecological world-building and political intrigue across the sands of Arrakis.",
            dateText = "3 days ago"
        ),
        Review(
            id = "rev_4",
            bookId = "book_8",
            userId = "user_patron_1",
            userName = "Marcus Aurelius",
            userRole = Role.PATRON,
            rating = 4,
            comment = "Spellbinding magical realism and generational tapestry of Macondo.",
            dateText = "Yesterday"
        )
    )

    private val _reviews = MutableStateFlow(initialReviews)
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // User's active checkouts
    private val _borrowedBookIds = MutableStateFlow(setOf("book_5")) // Marcus starts with Meditations borrowed
    val borrowedBookIds: StateFlow<Set<String>> = _borrowedBookIds.asStateFlow()

    fun switchUser(userId: String) {
        usersList.find { it.id == userId }?.let {
            _currentUser.value = it
        }
    }

    fun borrowBook(bookId: String): Result<String> {
        val currentBook = _books.value.find { it.id == bookId }
            ?: return Result.failure(Exception("Book not found"))

        if (currentBook.copiesAvailable <= 0) {
            return Result.failure(Exception("No copies currently available."))
        }

        if (_borrowedBookIds.value.contains(bookId)) {
            return Result.failure(Exception("You have already checked out this title."))
        }

        _borrowedBookIds.value = _borrowedBookIds.value + bookId
        _books.value = _books.value.map {
            if (it.id == bookId) it.copy(copiesAvailable = it.copiesAvailable - 1) else it
        }
        return Result.success("Successfully checked out '${currentBook.title}'!")
    }

    fun returnBook(bookId: String): Result<String> {
        val currentBook = _books.value.find { it.id == bookId }
            ?: return Result.failure(Exception("Book not found"))

        if (!_borrowedBookIds.value.contains(bookId)) {
            return Result.failure(Exception("You do not have this book checked out."))
        }

        _borrowedBookIds.value = _borrowedBookIds.value - bookId
        _books.value = _books.value.map {
            if (it.id == bookId) it.copy(copiesAvailable = (it.copiesAvailable + 1).coerceAtMost(it.copiesTotal)) else it
        }
        return Result.success("Returned '${currentBook.title}'. Thank you!")
    }

    fun addReview(bookId: String, rating: Int, comment: String): Result<String> {
        val user = _currentUser.value
        val hasReviewed = _reviews.value.any { it.bookId == bookId && it.userId == user.id }
        if (hasReviewed) {
            return Result.failure(Exception("You have already reviewed this book. In our schema, (bookId, userId) is unique."))
        }

        val newReview = Review(
            id = "rev_${UUID.randomUUID().toString().take(8)}",
            bookId = bookId,
            userId = user.id,
            userName = user.name,
            userRole = user.role,
            rating = rating.coerceIn(1, 5),
            comment = comment.trim(),
            dateText = "Just now"
        )
        _reviews.value = listOf(newReview) + _reviews.value
        return Result.success("Review published!")
    }

    fun addBook(
        title: String,
        author: String,
        isbn: String,
        publishedYear: Int,
        copies: Int,
        genreId: String,
        summary: String
    ): Result<String> {
        if (_currentUser.value.role != Role.LIBRARIAN) {
            return Result.failure(Exception("Access denied: Only Librarians can add catalog entries."))
        }
        if (title.isBlank() || author.isBlank() || isbn.isBlank()) {
            return Result.failure(Exception("Title, author, and ISBN are required."))
        }
        if (_books.value.any { it.isbn.equals(isbn.trim(), ignoreCase = true) }) {
            return Result.failure(Exception("A book with ISBN '$isbn' already exists. ISBN must be unique."))
        }

        val newBook = Book(
            id = "book_${UUID.randomUUID().toString().take(8)}",
            title = title.trim(),
            author = author.trim(),
            isbn = isbn.trim(),
            publishedYear = publishedYear,
            copiesTotal = copies.coerceAtLeast(1),
            copiesAvailable = copies.coerceAtLeast(1),
            genreId = genreId,
            summary = summary.trim()
        )
        _books.value = _books.value + newBook
        return Result.success("Book '${newBook.title}' added to catalog!")
    }

    fun updateBookCopies(bookId: String, newTotal: Int): Result<String> {
        if (_currentUser.value.role != Role.LIBRARIAN) {
            return Result.failure(Exception("Only librarians can adjust inventory."))
        }
        val book = _books.value.find { it.id == bookId }
            ?: return Result.failure(Exception("Book not found"))

        val total = newTotal.coerceAtLeast(1)
        val delta = total - book.copiesTotal
        val newAvailable = (book.copiesAvailable + delta).coerceIn(0, total)

        _books.value = _books.value.map {
            if (it.id == bookId) it.copy(copiesTotal = total, copiesAvailable = newAvailable) else it
        }
        return Result.success("Inventory updated: $total total copies.")
    }

    fun addGenre(name: String, description: String, colorHex: Long): Result<String> {
        if (_currentUser.value.role != Role.LIBRARIAN) {
            return Result.failure(Exception("Only librarians can create genres."))
        }
        if (name.isBlank()) {
            return Result.failure(Exception("Genre name cannot be blank."))
        }
        if (_genres.value.any { it.name.equals(name.trim(), ignoreCase = true) }) {
            return Result.failure(Exception("Genre '$name' already exists."))
        }

        val newGenre = Genre(
            id = "genre_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            description = description.trim(),
            colorHex = colorHex
        )
        _genres.value = _genres.value + newGenre
        return Result.success("Genre '${newGenre.name}' created.")
    }
}
