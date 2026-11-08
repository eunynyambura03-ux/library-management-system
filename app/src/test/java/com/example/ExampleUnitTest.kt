package com.example

import com.example.data.model.Role
import com.example.data.repository.LibraryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: LibraryRepository

    @Before
    fun setup() {
        repository = LibraryRepository()
    }

    @Test
    fun verifySeedCountsMeetAssignmentRequirements() {
        // Assignment requirement: at least 4 Genres and at least 15 Books
        val genres = repository.genres.value
        val books = repository.books.value

        assertTrue("Must have at least 4 genres (actual: ${genres.size})", genres.size >= 4)
        assertTrue("Must have at least 15 books (actual: ${books.size})", books.size >= 15)

        // Verify all 5 genres have books assigned to them
        genres.forEach { genre ->
            val count = books.count { it.genreId == genre.id }
            assertTrue("Genre ${genre.name} should have at least 1 book (actual: $count)", count > 0)
        }
    }

    @Test
    fun verifyBookForeignKeysAndUniqueIsbn() {
        val genres = repository.genres.value
        val genreIds = genres.map { it.id }.toSet()
        val books = repository.books.value

        // Every book's genreId must reference a valid Genre in genreIds
        books.forEach { book ->
            assertTrue("Book '${book.title}' has invalid genreId '${book.genreId}'", genreIds.contains(book.genreId))
        }

        // ISBN must be unique
        val isbns = books.map { it.isbn }
        assertEquals("All ISBNs must be unique", isbns.size, isbns.toSet().size)
    }

    @Test
    fun verifyCirculationBorrowAndReturn() {
        val testBookId = "book_1"
        val initialAvailable = repository.books.value.find { it.id == testBookId }!!.copiesAvailable

        // Patron borrows book
        val borrowResult = repository.borrowBook(testBookId)
        assertTrue("Borrow should succeed", borrowResult.isSuccess)
        val afterBorrow = repository.books.value.find { it.id == testBookId }!!.copiesAvailable
        assertEquals(initialAvailable - 1, afterBorrow)

        // Cannot borrow twice by same patron
        val secondBorrow = repository.borrowBook(testBookId)
        assertFalse("Cannot borrow same title twice", secondBorrow.isSuccess)

        // Return book
        val returnResult = repository.returnBook(testBookId)
        assertTrue("Return should succeed", returnResult.isSuccess)
        val afterReturn = repository.books.value.find { it.id == testBookId }!!.copiesAvailable
        assertEquals(initialAvailable, afterReturn)
    }

    @Test
    fun verifyLibrarianRolePermissions() {
        // Current user is Patron Marcus by default
        assertEquals(Role.PATRON, repository.currentUser.value.role)

        // Patron attempt to add book should fail
        val patronAddResult = repository.addBook(
            title = "Unauthorized Book",
            author = "Unknown",
            isbn = "978-9999999999",
            publishedYear = 2024,
            copies = 2,
            genreId = "genre_scitech",
            summary = "Test"
        )
        assertFalse("Patron cannot add book to catalog", patronAddResult.isSuccess)

        // Switch to Librarian Eleanor
        repository.switchUser("user_librarian_1")
        assertEquals(Role.LIBRARIAN, repository.currentUser.value.role)

        // Librarian should succeed
        val librarianAddResult = repository.addBook(
            title = "Authorized Book",
            author = "Known Author",
            isbn = "978-9999999999",
            publishedYear = 2024,
            copies = 2,
            genreId = "genre_scitech",
            summary = "Test"
        )
        assertTrue("Librarian can add book to catalog", librarianAddResult.isSuccess)
    }

    @Test
    fun verifyReviewUniqueConstraintPerUserPerBook() {
        val bookId = "book_2"
        val reviewResult1 = repository.addReview(bookId, 5, "Great book!")
        assertTrue("First review should succeed", reviewResult1.isSuccess)

        // Second review by same user for same book must fail
        val reviewResult2 = repository.addReview(bookId, 4, "Trying again!")
        assertFalse("Second review by same user on same book should fail", reviewResult2.isSuccess)
    }
}
