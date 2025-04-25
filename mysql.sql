-- Drop existing tables if they exist
-- DROP TABLE IF EXISTS Transactions;
-- DROP TABLE IF EXISTS Book;
--DROP TABLE IF EXISTS User;

-- User table with admin flag
-- CREATE TABLE User (
--     id INTEGER PRIMARY KEY,
--     username VARCHAR(50) UNIQUE NOT NULL,
--     password VARCHAR(255) NOT NULL,
--     fname VARCHAR(255) NOT NULL,
--     lname VARCHAR(255) NOT NULL,
--     isAdmin BOOLEAN DEFAULT FALSE,
--     fine DECIMAL(10,2) DEFAULT 0.0
-- );

-- Book table
-- CREATE TABLE Book (
--     isbn VARCHAR(255) NOT NULL,
--     title VARCHAR(255) NOT NULL,
--     author VARCHAR(255) NOT NULL,
--     isAvailable BOOLEAN NOT NULL,
--     copies INTEGER NOT NULL
-- );

-- Transaction table
-- CREATE TABLE Transactions (
--     transactionId INTEGER PRIMARY KEY AUTOINCREMENT,
--     userId INTEGER NOT NULL,
--     bookIsbn INTEGER NOT NULL,
--     borrowDate DATE NOT NULL,
--     returnDate DATE,
--     fine DECIMAL(10,2) DEFAULT 0.0,
--     FOREIGN KEY (userId) REFERENCES User(id),
--     FOREIGN KEY (bookIsbn) REFERENCES Book(isbn)
-- );

-- -- Insert admin user (password: admin123)
-- INSERT INTO User (id, username, password, fname, lname, isAdmin, fine) VALUES
-- (5, 'lancelot', 'tennis', 'lance', 'wilson', false, 14.0);

-- Insert some sample books
-- INSERT INTO Book (isbn, title, author, isAvailable, copies) VALUES
-- ('9780061120084', 'To Kill a Mockingbird', 'Harper Lee', true, 3),
-- ('9780141439518', 'Pride and Prejudice', 'Jane Austen', true, 2),
-- ('9780743273565', 'The Great Gatsby', 'F. Scott Fitzgerald', true, 4);
-- Create Reservations table
CREATE TABLE IF NOT EXISTS Reservations (
    reservationId INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    bookIsbn TEXT NOT NULL,
    reservationDate DATE NOT NULL,
    notificationDate DATE,
    isNotified BOOLEAN DEFAULT FALSE,
    isActive BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (userId) REFERENCES User(id),
    FOREIGN KEY (bookIsbn) REFERENCES Book(isbn)
);

-- Add index for faster lookups
CREATE INDEX IF NOT EXISTS idx_reservations_user ON Reservations(userId);
CREATE INDEX IF NOT EXISTS idx_reservations_book ON Reservations(bookIsbn);
CREATE INDEX IF NOT EXISTS idx_reservations_active ON Reservations(isActive);

-- Add notification system trigger
CREATE TRIGGER IF NOT EXISTS notify_reservations
AFTER UPDATE OF copies ON Book
WHEN NEW.copies > 0 AND OLD.copies = 0
BEGIN
    UPDATE Reservations
    SET isNotified = TRUE,
        notificationDate = CURRENT_DATE
    WHERE bookIsbn = NEW.isbn
    AND isActive = TRUE
    AND isNotified = FALSE;
END; 