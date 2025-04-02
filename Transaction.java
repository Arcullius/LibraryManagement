import java.time.LocalDate;

public class Transaction {

    //transaction variables
    private int transactionId;
    private User user;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private double fine;
    //acts as transactionId
    private static int numTransactions = 0;

    //constructor for current date
    public Transaction(User user, Book book) {
        this.transactionId = numTransactions;
        this.user = user;
        this.book = book;
        this.borrowDate = LocalDate.now();
        this.fine = 0.0;
        numTransactions++;
    }

    //constructor for custom date
    public Transaction(User user, Book book, LocalDate borrowDate) {
        this.transactionId = numTransactions;
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.fine = 0.0;
        numTransactions++;
    }

    //Implement getTransaction when we figure out file system
    /*public static Transaction getTransaction(int transactionId){

    }*/
}