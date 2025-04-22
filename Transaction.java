import java.sql.*;
import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private User user;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private double fine;
    private static FileManager db;

    public Transaction(int transactionId, User user, Book book) {
        this.transactionId = transactionId;
        this.user = user;
        this.book = book;
        this.borrowDate = LocalDate.now();
        this.fine = 0.0;
        
        if (db == null) {
            db = new FileManager();
        }
    }

    public Transaction(int transactionId, User user, Book book, LocalDate borrowDate) {
        this(transactionId, user, book);
        this.borrowDate = borrowDate;
    }

    // Getters
    public int getTransactionId() { return transactionId; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFine() { return fine; }

    // Database operations
    public void save() {
        try {
            String sql = "INSERT INTO Transactions (transactionId, userId, bookIsbn, borrowDate, returnDate, fine) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setInt(1, transactionId);
            ps.setInt(2, user.getId());
            ps.setInt(3, book.getIsbn());
            ps.setDate(4, java.sql.Date.valueOf(borrowDate));
            ps.setDate(5, returnDate != null ? java.sql.Date.valueOf(returnDate) : null);
            ps.setDouble(6, fine);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnBook() {
        this.returnDate = LocalDate.now();
        calculateFine();
        updateTransaction();
    }

    private void calculateFine() {
        if (returnDate != null) {
            long daysOverdue = returnDate.toEpochDay() - borrowDate.toEpochDay() - 14;
            if (daysOverdue > 0) {
                this.fine = daysOverdue;
            }
        }
    }

    private void updateTransaction() {
        try {
            String sql = "UPDATE Transactions SET returnDate = ?, fine = ? WHERE transactionId = ?";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(returnDate));
            ps.setDouble(2, fine);
            ps.setInt(3, transactionId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Transaction getTransaction(int userId, int bookIsbn) {
        try {
            String sql = "SELECT * FROM Transactions WHERE userId = ? AND bookIsbn = ? AND returnDate IS NULL";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, bookIsbn);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = User.login(rs.getString("username"), rs.getString("password"));
                Book book = Book.getBook(bookIsbn);
                Transaction transaction = new Transaction(
                    rs.getInt("transactionId"),
                    user,
                    book,
                    rs.getDate("borrowDate").toLocalDate()
                );
                transaction.fine = rs.getDouble("fine");
                if (rs.getDate("returnDate") != null) {
                    transaction.returnDate = rs.getDate("returnDate").toLocalDate();
                }
                ps.close();
                return transaction;
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transaction Id='" + transactionId + '\'' +
                ", User=" + user.getName() +
                ", Book=" + book.getTitle() +
                ", Borrow Date=" + borrowDate +
                ", Return Date=" + returnDate +
                ", Fine=" + fine +
                '}';
    }
} 


