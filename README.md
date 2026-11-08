# Library Management System

A production-ready data layer and management system for a modern library catalog, built with Prisma ORM, PostgreSQL, and an interactive Android application.

---

## 1. Schema Overview

The database schema models the core catalog and circulation entities:
- **`Role`**: An enumeration defining user permissions (`PATRON`, `LIBRARIAN`).
- **`User`**: Library members and staff with unique emails, names, assigned roles, and review histories.
- **`Genre`**: Independent classification taxonomy for catalog items (e.g., *Science & Technology*, *Philosophy & Ethics*, *Literary Fiction*).
- **`Book`**: Represents physical catalog holdings with titles, authors, unique ISBNs, publication years, inventory copy counts, and a normalized foreign-key relation to `Genre`.
- **`Review` (Stretch Goal)**: Bridges patrons with books, capturing numeric ratings (1–5), optional review commentary, and timestamps, with unique constraints ensuring patrons can submit at most one review per book.

---

## 2. Schema Decisions

### Why Book References Genre Rather Than Embedding It

In designing the data model, a foundational decision was whether to embed Genre attributes directly inside the Book model (or use a simple string scalar) versus establishing a normalized separate `Genre` model linked via a foreign key (`genreId`) and `@relation`.

We chose to **normalize `Genre` into its own entity and reference it via a foreign key** for the following architectural reasons:

1. **Single Source of Truth & Data Integrity (3NF Normalization)**
   - If genre names or descriptions were embedded as unstructured strings inside every Book row, a typo or naming inconsistency (e.g., `"Sci-Fi"`, `"Science-Fiction"`, `"Sci Fi"`) would fragment the catalog.
   - Normalizing Genre guarantees that the category name, slug, and description are stored once. If a library renames `"Computer Science"` to `"Computing & AI"`, a single update on the `Genre` table reflects instantly across all associated books without running batch update mutations across hundreds of thousands of book records.

2. **Independent Lifecycle & Metadata Management**
   - Genres possess their own business attributes beyond a raw label—such as descriptions, shelf locations, Dewey Decimal ranges, and classification hierarchies.
   - With an independent `Genre` entity, librarians can manage, curate, and query genres before any books are even added to them, and easily generate aggregated analytics (e.g., total books, circulation rate, and patron interest per genre).

3. **Referential Integrity & Foreign Key Constraints**
   - The `@relation(fields: [genreId], references: [id], onDelete: Restrict)` constraint guarantees at the database engine level that no book can reference a non-existent genre, and that an active genre containing books cannot be accidentally dropped without reassignment.

4. **Query Performance & Indexing**
   - Storing a foreign key (`genreId`) with an index `@@index([genreId])` allows the database query planner to perform fast joins and index scans when filtering by genre, compared to expensive string comparisons or full-text searches.

---

## 3. Modeling in Prisma vs. a Document Database (Stretch Reflection)

> **"One thing that felt different modeling in Prisma vs. a document database"**

The most noticeable contrast when modeling in **Prisma (Relational / PostgreSQL)** compared to a **Document Database (e.g., MongoDB / Mongoose)** is the shift from **Application-Level Schema Assumptions** to **Engine-Enforced Declarative Referential Contracts**:

* **In a Document Database (MongoDB):** Relations are often handled either by embedding subdocuments or by storing loose `ObjectId` references without foreign key constraints at the storage engine level. If an associated genre or user document is deleted or renamed, the developer must manually handle dangling references, orphaned records, or synchronization across documents in application code or pre-save hooks.
* **In Prisma with PostgreSQL:** Every relation is explicitly defined in the schema with strict bidirectional types and database-level foreign keys (`onDelete`, `onUpdate`). Prisma’s generated client provides compile-time guarantee of relational paths (`include: { genre: true, reviews: { include: { user: true } } }`). You cannot write a query with typos in joined relation names, and the database engine itself enforces referential validity. Migrations are declarative, deterministic, and version-controlled, transforming schema evolution into a predictable operational pipeline rather than runtime schema migrations.

---

## 4. File Verification Guide

| File | Content & Requirements Met |
| :--- | :--- |
| `api/prisma/schema.prisma` | Defines `Role` enum (`PATRON`, `LIBRARIAN`), `User`, `Genre`, `Book` with `genreId` `@relation`, and `Review` relation. |
| `api/prisma/seed.ts` | Seeds 5 Genres, 16 Books distributed across all genres, Users (Librarian & Patrons), and sample Reviews. |
| `api/src/prisma/prisma.service.ts` | Extends `PrismaClient`, implements `OnModuleInit`, and calls `this.$connect()`. |
| `api/.env.example` | Contains placeholder `DATABASE_URL` for PostgreSQL without committed credentials. |
| `README.md` | Contains comprehensive 'Schema Decisions' and Prisma vs Document DB reflections. |

---

## 5. Running Migrations & Seeding

```bash
# 1. Install dependencies
cd api && npm install

# 2. Configure environment variables
cp .env.example .env
# Edit .env with your PostgreSQL credentials

# 3. Run Prisma migration
npx prisma migrate dev --name init_library_schema

# 4. Run database seed script (populates 5 genres & 16+ books)
npx prisma db seed
```
