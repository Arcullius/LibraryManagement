import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private User user;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private double fine;

    public Transaction(int transactionId, User user, Book book) {
        this.transactionId = transactionId;
        this.user = user;
        this.book = book;
        this.borrowDate = LocalDate.now();
        this.fine = 0.0;
    }

    public Transaction(int transactionId, User user, Book book, LocalDate borrowDate) {
        this.transactionId = transactionId;
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.fine = 0.0;
    }
    public void returnBook() {
        this.returnDate = LocalDate.now();
        calculateFine();
    }

    private void calculateFine() {
        if (returnDate != null) {
            //using long to avoide overflow
            long daysOverdue = returnDate.toEpochDay() - borrowDate.toEpochDay() - 14;
            if (daysOverdue > 0) {
                this.fine = daysOverdue;
            }
        }
    }

    // Getters
    public int getTransactionId() { return transactionId; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFine() { return fine; }

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


