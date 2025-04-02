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
}