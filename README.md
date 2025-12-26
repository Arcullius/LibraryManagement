# Library Management System

A comprehensive Java-based library management system that enables users to manage books, user accounts, reservations, and library transactions.

## Features

- **User Authentication**: Create accounts and login with role-based access (Admin/Regular Users)
- **Book Management**: Browse, search, borrow, return, and reserve books
- **User Management**: Admin can manage users and user records
- **Transaction History**: Track all library transactions (borrows, returns, etc.)
- **Reservation System**: Users can reserve books that are currently unavailable
- **Statistics**: View library statistics and usage reports
- **Data Persistence**: All data is stored in a SQLite database

## Project Structure

```
LibraryManagement/
├── Main.java                 # Application entry point
├── Library.java              # Core library management logic
├── User.java                 # User entity and management
├── Book.java                 # Book entity and management
├── IBook.java                # Book interface
├── Reservation.java          # Reservation handling
├── Transaction.java          # Transaction records
├── FileManager.java          # Database operations and file handling
├── Statistics.java           # Library statistics
├── Books.csv                 # Sample book data
├── Users.csv                 # Sample user data
├── mysql.sql                 # Database schema
├── Decisions.md              # Architecture and design decisions
├── run.bat                   # Windows batch file to run the application
└── README.md                 # This file
```

## Requirements

- Java 8 or higher
- SQLite JDBC driver (included in project setup)

## Getting Started

### 1. Clone or Download the Project
```bash
git clone <repository-url>
cd LibraryManagement-master
```

### 2. Compile the Project
```bash
javac *.java
```

### 3. Run the Application

**On Windows:**
```bash
run.bat
```

**On Linux/Mac:**
```bash
java Main
```

## Usage

### Login Menu
When you start the application, you'll see the login menu with these options:
- **Login**: Sign in with existing credentials
- **Create Account**: Register a new user account
- **Exit**: Close the application

### User Menu
Regular users can:
- View available books
- Borrow books
- Return books
- Reserve books
- View their transaction history
- Check reservations
- Logout

### Admin Menu
Administrators can:
- Manage books (add, update, delete)
- Manage users
- View all transactions
- View library statistics
- Manage reservations
- Logout

## Database

The system uses SQLite for data persistence. The database schema includes:

- **Books Table**: Stores book information (ISBN, title, author, copies)
- **Users Table**: Stores user information (ID, name, password, role)
- **Transactions Table**: Records all borrowing and returning activities
- **Reservations Table**: Manages book reservations

## File Structure

- **Books.csv**: Pre-loaded sample books data
- **Users.csv**: Pre-loaded sample users data
- **mysql.sql**: Complete database schema for reference

## Key Classes

### Main.java
Entry point of the application that initializes the Library system.

### Library.java
Core class managing:
- User authentication and login
- Menu navigation
- Admin and user operations
- Overall system flow

### User.java
Represents a user entity with:
- User credentials
- User roles (Admin/Regular)
- Account management

### Book.java
Represents a book entity with:
- Book details (ISBN, title, author)
- Availability status
- Copy count management

### FileManager.java
Handles all database operations:
- CRUD operations for books, users, and transactions
- Database connection management
- Query execution

### Reservation.java
Manages book reservations:
- Reserve book functionality
- Cancel reservations
- View reservations

### Transaction.java
Records library transactions:
- Borrow records
- Return records
- Transaction history

### Statistics.java
Generates library statistics:
- Most borrowed books
- Active users
- Library usage reports

## Sample Data

The system comes with sample data in CSV format:
- Sample books in `Books.csv`
- Sample users in `Users.csv`

## Design Decisions

See [Decisions.md](Decisions.md) for detailed information about:
- Database choice (SQLite)
- Class structure and architecture
- Design patterns used
- Trade-offs and considerations

## Future Enhancements

- GUI interface (Swing/JavaFX)
- Email notifications for due dates
- Fine calculation system
- Advanced search filters
- Bulk import/export functionality
- Integration with external book databases

## Troubleshooting

### Database Connection Issues
Ensure the SQLite JDBC driver is properly configured and the database file has appropriate permissions.

### Compilation Errors
Make sure all Java files are in the same directory and you're using Java 8 or higher.

## License

This project was created as a semester project for Messiah University.

## Author

Created during 1st Year, Semester 2 at Messiah University.

---

For questions or issues, please refer to the project documentation or contact the development team.
