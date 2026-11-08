import { PrismaClient, Role } from '@prisma/client';

const prisma = new PrismaClient();

async function main() {
  console.log('Seeding Library Management System database...');

  // Clean existing records in reverse dependency order
  await prisma.review.deleteMany();
  await prisma.book.deleteMany();
  await prisma.genre.deleteMany();
  await prisma.user.deleteMany();

  // 1. Seed Users (Patrons and Librarian)
  const librarian = await prisma.user.create({
    data: {
      email: 'librarian@libraryhub.org',
      name: 'Eleanor Vance',
      role: Role.LIBRARIAN,
    },
  });

  const patron1 = await prisma.user.create({
    data: {
      email: 'marcus.aurelius@reader.org',
      name: 'Marcus Aurelius',
      role: Role.PATRON,
    },
  });

  const patron2 = await prisma.user.create({
    data: {
      email: 'ada.lovelace@reader.org',
      name: 'Ada Lovelace',
      role: Role.PATRON,
    },
  });

  console.log(`Seeded users: 1 Librarian, 2 Patrons`);

  // 2. Seed at least 4 Genres (We seed 5 distinct genres)
  const genresData = [
    {
      name: 'Science & Technology',
      description: 'Foundations of computer science, engineering, physics, and future innovations.',
    },
    {
      name: 'Philosophy & Ethics',
      description: 'Classical logic, moral philosophy, epistemological inquiries, and mindful living.',
    },
    {
      name: 'Literary Fiction',
      description: 'Acclaimed contemporary and classic prose exploring the human condition.',
    },
    {
      name: 'Mystery & Thriller',
      description: 'Suspenseful detective stories, forensic mysteries, and psychological puzzles.',
    },
    {
      name: 'Science Fiction & Fantasy',
      description: 'Speculative worlds, cosmic exploration, epic sagas, and cybernetic futures.',
    },
  ];

  const genres = await Promise.all(
    genresData.map((g) =>
      prisma.genre.create({
        data: g,
      })
    )
  );

  console.log(`Seeded ${genres.length} Genres.`);

  const [sciTech, philosophy, litFiction, mystery, sciFi] = genres;

  // 3. Seed at least 15 Books distributed across those genres (16 total books)
  const booksData = [
    // Science & Technology (4 books)
    {
      title: 'Designing Data-Intensive Applications',
      author: 'Martin Kleppmann',
      isbn: '978-1449373320',
      publishedYear: 2017,
      copiesTotal: 4,
      copiesAvailable: 3,
      genreId: sciTech.id,
    },
    {
      title: 'Structure and Interpretation of Computer Programs',
      author: 'Harold Abelson & Gerald Jay Sussman',
      isbn: '978-0262510875',
      publishedYear: 1996,
      copiesTotal: 3,
      copiesAvailable: 2,
      genreId: sciTech.id,
    },
    {
      title: 'Clean Code: A Handbook of Agile Software Craftsmanship',
      author: 'Robert C. Martin',
      isbn: '978-0132350884',
      publishedYear: 2008,
      copiesTotal: 5,
      copiesAvailable: 5,
      genreId: sciTech.id,
    },
    {
      title: 'The Pragmatic Programmer',
      author: 'David Thomas & Andrew Hunt',
      isbn: '978-0135957059',
      publishedYear: 2019,
      copiesTotal: 4,
      copiesAvailable: 4,
      genreId: sciTech.id,
    },

    // Philosophy & Ethics (3 books)
    {
      title: 'Meditations',
      author: 'Marcus Aurelius',
      isbn: '978-0140449334',
      publishedYear: 2006,
      copiesTotal: 6,
      copiesAvailable: 4,
      genreId: philosophy.id,
    },
    {
      title: 'Nicomachean Ethics',
      author: 'Aristotle',
      isbn: '978-0199213610',
      publishedYear: 2009,
      copiesTotal: 3,
      copiesAvailable: 3,
      genreId: philosophy.id,
    },
    {
      title: 'Justice: What is the Right Thing to Do?',
      author: 'Michael J. Sandel',
      isbn: '978-0374532505',
      publishedYear: 2010,
      copiesTotal: 3,
      copiesAvailable: 2,
      genreId: philosophy.id,
    },

    // Literary Fiction (3 books)
    {
      title: 'One Hundred Years of Solitude',
      author: 'Gabriel García Márquez',
      isbn: '978-0060883287',
      publishedYear: 1967,
      copiesTotal: 4,
      copiesAvailable: 3,
      genreId: litFiction.id,
    },
    {
      title: 'The Remains of the Day',
      author: 'Kazuo Ishiguro',
      isbn: '978-0679731726',
      publishedYear: 1989,
      copiesTotal: 3,
      copiesAvailable: 3,
      genreId: litFiction.id,
    },
    {
      title: 'Crime and Punishment',
      author: 'Fyodor Dostoevsky',
      isbn: '978-0140449136',
      publishedYear: 1866,
      copiesTotal: 4,
      copiesAvailable: 2,
      genreId: litFiction.id,
    },

    // Mystery & Thriller (3 books)
    {
      title: 'The Hound of the Baskervilles',
      author: 'Arthur Conan Doyle',
      isbn: '978-0141439242',
      publishedYear: 1902,
      copiesTotal: 4,
      copiesAvailable: 4,
      genreId: mystery.id,
    },
    {
      title: 'And Then There Were None',
      author: 'Agatha Christie',
      isbn: '978-0062073488',
      publishedYear: 1939,
      copiesTotal: 5,
      copiesAvailable: 3,
      genreId: mystery.id,
    },
    {
      title: 'The Girl with the Dragon Tattoo',
      author: 'Stieg Larsson',
      isbn: '978-0307949486',
      publishedYear: 2005,
      copiesTotal: 3,
      copiesAvailable: 2,
      genreId: mystery.id,
    },

    // Science Fiction & Fantasy (3 books)
    {
      title: 'Dune',
      author: 'Frank Herbert',
      isbn: '978-0441172719',
      publishedYear: 1965,
      copiesTotal: 6,
      copiesAvailable: 5,
      genreId: sciFi.id,
    },
    {
      title: 'Neuromancer',
      author: 'William Gibson',
      isbn: '978-0441569595',
      publishedYear: 1984,
      copiesTotal: 4,
      copiesAvailable: 4,
      genreId: sciFi.id,
    },
    {
      title: 'Snow Crash',
      author: 'Neal Stephenson',
      isbn: '978-0553380958',
      publishedYear: 1992,
      copiesTotal: 3,
      copiesAvailable: 3,
      genreId: sciFi.id,
    },
  ];

  const books = await Promise.all(
    booksData.map((book) =>
      prisma.book.create({
        data: book,
      })
    )
  );

  console.log(`Seeded ${books.length} Books across 5 Genres.`);

  // 4. Seed Reviews (Stretch task)
  await prisma.review.createMany({
    data: [
      {
        bookId: books[0].id, // Designing Data-Intensive Applications
        userId: patron2.id, // Ada Lovelace
        rating: 5,
        comment: 'The definitive masterclass on distributed systems and storage architectures!',
      },
      {
        bookId: books[4].id, // Meditations
        userId: patron1.id, // Marcus Aurelius
        rating: 5,
        comment: 'Personal reflections on stoicism and steadfast reason.',
      },
      {
        bookId: books[13].id, // Dune
        userId: patron2.id, // Ada Lovelace
        rating: 5,
        comment: 'Immense ecological world-building and political intrigue.',
      },
      {
        bookId: books[7].id, // One Hundred Years of Solitude
        userId: patron1.id, // Marcus Aurelius
        rating: 4,
        comment: 'Spellbinding magical realism and generational tapestry.',
      },
    ],
  });

  console.log('Seeded initial reviews.');
  console.log('Database seeding completed successfully.');
}

main()
  .catch((e) => {
    console.error('Seeding error:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
