import java.util.ArrayList;
public class Library {
    private ArrayList<Book> books;
    private ArrayList<User> users;
    private ArrayList<Transaction> transactions;
    private int maxBorrowLimit;

    public Library() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.maxBorrowLimit = 3;
    }

    // Book Management
    public void addBook(Book book) {
        books.add(book);
    }
    public void removeBook(int isbn) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn()==(isbn)) {
                books.remove(i);
                break;
            }
        }
    }
    public Book getBook(int isbn){
        for (Book book : books) {
            if (book.getIsbn()==(isbn)) {
                return book;
            }
        }
        return null;
    }
    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books);
    }
   
    
    }



