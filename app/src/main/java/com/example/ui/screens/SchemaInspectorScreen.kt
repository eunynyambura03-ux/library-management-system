package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldAvailable

@Composable
fun SchemaInspectorScreen(
    onCopyMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        "Prisma Schema",
        "ER Diagram",
        "Schema Decisions",
        "Prisma vs Doc DB",
        "Prisma Service & Seed"
    )

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.testTag("schema_tab_$index")
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedSubTab) {
                0 -> {
                    // TAB 0: PRISMA SCHEMA CODE VIEWER
                    item {
                        SchemaVerificationHeader()
                    }
                    item {
                        PrismaCodeViewer(
                            title = "api/prisma/schema.prisma",
                            code = PRISMA_SCHEMA_CODE,
                            onCopy = {
                                copyToClipboard(context, PRISMA_SCHEMA_CODE)
                                onCopyMessage("Copied schema.prisma to clipboard!")
                            }
                        )
                    }
                }
                1 -> {
                    // TAB 1: ER DIAGRAM
                    item {
                        EntityRelationshipDiagram()
                    }
                }
                2 -> {
                    // TAB 2: SCHEMA DECISIONS
                    item {
                        SchemaDecisionsExplanation()
                    }
                }
                3 -> {
                    // TAB 3: PRISMA VS DOCUMENT DB
                    item {
                        PrismaVsDocumentDbNote()
                    }
                }
                4 -> {
                    // TAB 4: PRISMA SERVICE & SEED
                    item {
                        PrismaCodeViewer(
                            title = "api/src/prisma/prisma.service.ts",
                            code = PRISMA_SERVICE_CODE,
                            onCopy = {
                                copyToClipboard(context, PRISMA_SERVICE_CODE)
                                onCopyMessage("Copied prisma.service.ts to clipboard!")
                            }
                        )
                    }
                    item {
                        SeedSummaryCard()
                    }
                }
            }
        }
    }
}

@Composable
fun SchemaVerificationHeader() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = EmeraldAvailable,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Assignment Requirements Verified",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "✓ Role enum (PATRON, LIBRARIAN)\n✓ User model using Role\n✓ Genre model\n✓ Book model referencing Genre via genreId foreign key with @relation\n✓ Stretch: Review model (bookId, userId, rating, comment)",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun PrismaCodeViewer(
    title: String,
    code: String,
    onCopy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = Color(0xFFE2E8F0),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }

                FilledTonalButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("copy_code_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = code,
                    color = Color(0xFFCBD5E1),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .padding(12.dp)
                        .horizontalScroll(rememberScrollState())
                )
            }
        }
    }
}

@Composable
fun EntityRelationshipDiagram() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Relational Architecture & Foreign Keys",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // GENRE
        ModelEntityCard(
            modelName = "Genre",
            subtitle = "Taxonomy Entity (One-to-Many with Books)",
            fields = listOf(
                "id: String @id @default(cuid()) [PK]",
                "name: String @unique",
                "description: String?",
                "books: Book[] (1 -> Many)"
            ),
            accentColor = Color(0xFF0284C7)
        )

        RelationshipConnector(label = "1 Genre has Many Books via genreId FK")

        // BOOK
        ModelEntityCard(
            modelName = "Book",
            subtitle = "Catalog Entity (References Genre via Foreign Key)",
            fields = listOf(
                "id: String @id @default(cuid()) [PK]",
                "title: String",
                "author: String",
                "isbn: String @unique",
                "publishedYear: Int?",
                "copiesTotal: Int @default(1)",
                "copiesAvailable: Int @default(1)",
                "genreId: String [Foreign Key]",
                "genre: Genre @relation(fields: [genreId], references: [id])",
                "reviews: Review[] (Stretch relation)"
            ),
            accentColor = Color(0xFFD97706)
        )

        RelationshipConnector(label = "1 Book has Many Reviews via bookId FK")

        // REVIEW
        ModelEntityCard(
            modelName = "Review (Stretch Task)",
            subtitle = "Bridges User & Book with Rating & Comments",
            fields = listOf(
                "id: String @id @default(cuid()) [PK]",
                "rating: Int (1 to 5)",
                "comment: String?",
                "bookId: String [FK -> Book.id]",
                "userId: String [FK -> User.id]",
                "@@unique([bookId, userId]) [1 review per user/book]"
            ),
            accentColor = Color(0xFF7C3AED)
        )

        RelationshipConnector(label = "Belongs to 1 User via userId FK")

        // USER
        ModelEntityCard(
            modelName = "User",
            subtitle = "Authentication & Authorization Entity",
            fields = listOf(
                "id: String @id @default(cuid()) [PK]",
                "email: String @unique",
                "name: String",
                "role: Role @default(PATRON) [PATRON | LIBRARIAN]",
                "reviews: Review[] (1 -> Many)"
            ),
            accentColor = EmeraldAvailable
        )
    }
}

@Composable
fun ModelEntityCard(
    modelName: String,
    subtitle: String,
    fields: List<String>,
    accentColor: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp, 28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "model $modelName",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    fields.forEach { field ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (field.contains("[PK]") || field.contains("[FK") || field.contains("Role")) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.outline)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = field,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RelationshipConnector(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountTree,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SchemaDecisionsExplanation() {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Schema Decisions: Foreign Key vs Embedding",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Why Book references Genre rather than embedding it",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            DecisionItem(
                number = "1",
                title = "Data Normalization & Single Source of Truth (3NF)",
                body = "Embedding genre names directly inside books creates data duplication across thousands of rows. If a genre name is modified or reclassified, embedding requires an expensive table-wide UPDATE operation. With a normalized Genre model, updating the name or description in one row instantly reflects across all associated books."
            )

            DecisionItem(
                number = "2",
                title = "Preventing Data Corruption & Typo Fragmentation",
                body = "Free-form strings or embedded documents lead to typos like 'SciFi', 'Science Fiction', and 'Sci-Fi' fragmenting catalog searches. A foreign key to Genre guarantees category taxonomy consistency."
            )

            DecisionItem(
                number = "3",
                title = "Independent Genre Lifecycle & Rich Metadata",
                body = "Genres have independent business logic: shelf location, Dewey Decimal system numbers, descriptions, and curator assignments. An independent entity allows librarians to curate genres before books are even added."
            )

            DecisionItem(
                number = "4",
                title = "Database-Enforced Referential Integrity",
                body = "Prisma's @relation with onDelete: Restrict ensures at the SQL database engine level that no book can reference a non-existent genre, and active genres with catalog items cannot be deleted accidentally."
            )

            DecisionItem(
                number = "5",
                title = "Query Performance & B-Tree Indexing",
                body = "Indexing genreId (@@index([genreId])) yields O(log N) indexed lookups and fast joins, compared to full-text or sequential scans on embedded strings."
            )
        }
    }
}

@Composable
fun DecisionItem(number: String, title: String, body: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = body,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PrismaVsDocumentDbNote() {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Stretch Task Note",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Modeling in Prisma vs. a Document Database",
                        fontSize = 12.sp,
                        color = Color(0xFF7C3AED)
                    )
                }
            }

            Text(
                text = "The core difference lies in Engine-Enforced Declarative Referential Contracts vs Application-Level Assumptions:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "1. Strict Referential Guarantees:\nIn document databases like MongoDB, relations between collections are typically loose ObjectId strings. If an author or genre document is deleted, application code must handle cleanup to prevent dangling references. In Prisma/PostgreSQL, foreign keys and onDelete constraints are physically enforced by the database engine.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "2. Compile-Time Type Safety:\nPrisma generates fully typed TypeScript clients based on the relational schema. Navigating relations with `include: { genre: true, reviews: true }` guarantees type safety down to each nested field, eliminating runtime undefined errors that often plague document aggregation pipelines.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "3. Deterministic Migrations:\nPrisma migrations provide version-controlled SQL migration steps (`prisma migrate dev`). In document databases, schema evolution is typically done lazily in application code or via custom maintenance scripts.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SeedSummaryCard() {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Seed Script Verification (api/prisma/seed.ts)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• 5 Genres created (Science & Tech, Philosophy, Literary Fiction, Mystery, Sci-Fi)\n• 16 Books inserted with foreign key genreId across all 5 genres\n• 3 Users created with Role enum (1 Librarian, 2 Patrons)\n• 4 Seed Reviews created connecting patrons with books (Stretch Goal)",
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Prisma Schema", text)
    clipboard.setPrimaryClip(clip)
}

const val PRISMA_SCHEMA_CODE = """generator client {
  provider = "prisma-client-js"
}

datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

enum Role {
  PATRON
  LIBRARIAN
}

model User {
  id        String   @id @default(cuid())
  email     String   @unique
  name      String
  role      Role     @default(PATRON)
  reviews   Review[]
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt

  @@map("users")
}

model Genre {
  id          String   @id @default(cuid())
  name        String   @unique
  description String?
  books       Book[]
  createdAt   DateTime @default(now())
  updatedAt   DateTime @updatedAt

  @@map("genres")
}

model Book {
  id              String   @id @default(cuid())
  title           String
  author          String
  isbn            String   @unique
  publishedYear   Int?
  copiesTotal     Int      @default(1)
  copiesAvailable Int      @default(1)
  
  genreId         String
  genre           Genre    @relation(fields: [genreId], references: [id], onDelete: Restrict)

  reviews         Review[] // Stretch task

  createdAt       DateTime @default(now())
  updatedAt       DateTime @updatedAt

  @@index([genreId])
  @@map("books")
}

model Review {
  id        String   @id @default(cuid())
  rating    Int      // 1 to 5 stars
  comment   String?
  
  bookId    String
  book      Book     @relation(fields: [bookId], references: [id], onDelete: Cascade)

  userId    String
  user      User     @relation(fields: [userId], references: [id], onDelete: Cascade)

  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt

  @@unique([bookId, userId])
  @@map("reviews")
}"""

val PRISMA_SERVICE_CODE = """import { Injectable, OnModuleInit, OnModuleDestroy } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class PrismaService extends PrismaClient implements OnModuleInit, OnModuleDestroy {
  async onModuleInit() {
    await this.${'$'}connect();
  }

  async onModuleDestroy() {
    await this.${'$'}disconnect();
  }
}"""
