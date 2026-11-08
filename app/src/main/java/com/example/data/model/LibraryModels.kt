package com.example.data.model

/**
 * User authorization roles matching the Prisma Role enum
 */
enum class Role {
    PATRON,
    LIBRARIAN
}

/**
 * User entity representing active patrons and librarians
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val avatarInitials: String = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
)

/**
 * Genre classification model matching Prisma Genre entity
 */
data class Genre(
    val id: String,
    val name: String,
    val description: String,
    val colorHex: Long = 0xFF4F46E5
)

/**
 * Book catalog item referencing a Genre via genreId foreign key
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String,
    val publishedYear: Int,
    val copiesTotal: Int,
    val copiesAvailable: Int,
    val genreId: String,
    val summary: String = ""
) {
    val isAvailable: Boolean get() = copiesAvailable > 0
}

/**
 * Review model (Stretch Task) referencing Book and User
 */
data class Review(
    val id: String,
    val bookId: String,
    val userId: String,
    val userName: String,
    val userRole: Role,
    val rating: Int, // 1 to 5
    val comment: String,
    val dateText: String = "Recent"
)
