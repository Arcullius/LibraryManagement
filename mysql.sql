-- Book table
CREATE TABLE Book (
    isbn INT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isAvailable BOOLEAN NOT NULL,
    copies INT NOT NULL
);

-- User table
CREATE TABLE User (
    id INT PRIMARY KEY,
    fname VARCHAR(255) NOT NULL,
    lname VARCHAR(255) NOT NULL,
    fine DECIMAL(10,2) DEFAULT 0.0
);

-- Transaction table
CREATE TABLE Transactions (
    transactionId INT PRIMARY KEY,
    userId INT NOT NULL,
    bookIsbn INT NOT NULL,
    borrowDate DATE NOT NULL,
    returnDate DATE,
    fine DECIMAL(10,2) DEFAULT 0.0,
    FOREIGN KEY (userId) REFERENCES User(id),
    FOREIGN KEY (bookIsbn) REFERENCES Book(isbn)
); 

INSERT INTO Book (isbn, title, author, isAvailable, copies) VALUES
(9780061120084, 'To Kill a Mockingbird', 'Harper Lee', true, 3);


-- Insert sample users
INSERT INTO User (id, fname, lname, fine) VALUES
(1001, 'John', 'Smith', 0.0);

-- Insert sample transactions
INSERT INTO Transactions (transactionId, userId, bookIsbn, borrowDate, returnDate, fine) VALUES
(2001, 1001, 9780061120084, '2024-01-15', '2024-01-29', 0.0);
