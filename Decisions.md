# Library Management System

## Implementation Decisions

### Database Choice: SQLite
#### Why SQLite?
- **Persistence**: Data persists between program runs
- **Relational Structure**: Natural fit for library management (books, users, transactions)
- Ensures data integrity
- **Lightweight**: No separate server required
- **Cross-platform**: Works on all major operating systems

#### Tradeoffs:
- **Pros**:
  - Data persistence between sessions
  - Better scalability for larger libraries
  - Built-in data integrity through foreign keys
  - Efficient querying for complex operations
  - Concurrent access support
- **Cons**:
  - More complex setup than in-memory storage
  - Requires SQL knowledge
  - Slightly slower for very small datasets
  - Additional dependency (SQLite JDBC driver)

### Class Structure

#### Book Class
```java
Book(int isbn, String title, String author, int copies)
```
- **Properties**:
  - `isbn`: Unique identifier (PRIMARY KEY)
  - `title`: Book title
  - `author`: Book author
  - `isAvailable`: Derived from copies count
  - `copies`: Number of available copies
- **Database Operations**:
  - `save()`: Insert/update book in database
  - `updateBook()`: Update existing book
  - `getBook()`: Retrieve book by ISBN
  - `deleteBook()`: Remove book from database

#### User Class
```java
User(int id, String fname, String lname)
```
- **Properties**:
  - `id`: Unique identifier (PRIMARY KEY)
  - `fname`: First name
  - `lname`: Last name
  - `fine`: Accumulated fines
- **Database Operations**:
  - `save()`: Insert/update user
  - `updateUser()`: Update user details
  - `getUser()`: Retrieve user by ID
  - `deleteUser()`: Remove user
  - `getBooksBorrowed()`: Get currently borrowed books via JOIN query

#### Transaction Class
```java
Transaction(int transactionId, User user, Book book)
Transaction(int transactionId, User user, Book book, LocalDate borrowDate)
```
- **Properties**:
  - `transactionId`: Unique identifier (PRIMARY KEY)
  - `user`: Reference to User (FOREIGN KEY)
  - `book`: Reference to Book (FOREIGN KEY)
  - `borrowDate`: Date book was borrowed
  - `returnDate`: Date book was returned (nullable)
  - `fine`: Calculated fine for late returns
- **Database Operations**:
  - `save()`: Insert/update transaction
  - `returnBook()`: Update return date and calculate fine
  - `getTransaction()`: Retrieve transaction by ID
  - `deleteTransaction()`: Remove transaction

### Database Schema
```sql
CREATE TABLE Book (
    isbn INT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isAvailable BOOLEAN NOT NULL,
    copies INT NOT NULL
);

CREATE TABLE User (
    id INT PRIMARY KEY,
    fname VARCHAR(255) NOT NULL,
    lname VARCHAR(255) NOT NULL,
    fine DECIMAL(10,2) DEFAULT 0.0
);

CREATE TABLE BookTransaction (
    transactionId INT PRIMARY KEY,
    userId INT NOT NULL,
    bookIsbn INT NOT NULL,
    borrowDate DATE NOT NULL,
    returnDate DATE,
    fine DECIMAL(10,2) DEFAULT 0.0,
    FOREIGN KEY (userId) REFERENCES User(id),
    FOREIGN KEY (bookIsbn) REFERENCES Book(isbn)
);
```

### Future Considerations
1. **Interlibrary Loan Implementation**:
   - Add new tables for interlibrary transactions
   - Track loan status between libraries
   - Handle shipping and return logistics

2. **Performance Optimization**:
   - Add indexes for frequently queried columns
   - Implement connection pooling
   - Add caching layer for frequently accessed data

3. **Security Enhancements**:
   - Add user authentication
   - Implement role-based access control
   - Encrypt sensitive data

4. **Scalability Improvements**:
   - Consider migration to client-server database
   - Implement database sharding for large datasets
   - Add replication for high availability

## Plan: 
### Book Class (with all book properites):
#### Constructor: 
      
      Book(int isbn, String title, String author, int copies)
    
### User Class (with all user properties utilizing arraylists):
#### Constructor:
      
      User(int id, String fname, String lname)
      
There will be a double fine variable and borrowedbooks arraylist within the constructor as well
### Transaction Class (handle all transactions utilizing java.Time.LocalDate)
 #### Constructors: 
 
       Transaction(int transactionId, User user, Book book) {
              this.transactionId = transactionId;
              this.user = user;
              this.book = book;
              this.borrowDate = LocalDate.now();
              this.fine = 0.0;
        
      Transaction(int transactionId, User user, Book book, LocalDate borrowDate) 
### Library Class (the actual library itself. utilzes all other classes and will be the bulk of the code)
 #### Constructors: 
      public Library() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.maxBorrowLimit = 3;
    }

This is a temporary model of what the class will look like pre-interlibrary loan implementation.


    
             



      
              

      
