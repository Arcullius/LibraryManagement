import java.util.ArrayList;
public class User{   
    //user variables
    private int id;
    private String name;
    private double fine;
    private ArrayList<Book> booksBorrowed;

    //constructor
    public User(int id, String name){
        this.id = id;
        this.name = name;
        this.fine = 0;
        this.booksBorrowed = new ArrayList<>();
    }

    //get methods
    public int getId() { return id; }
    public String getName() { return name; }
    public ArrayList<Book> getBooksBorrowed() { return booksBorrowed; }
    public double getFine() { return fine; }

    //set methods
    public void setId(int id){ this.id = id; }
    public void setName(String name){ this.name = name; }
    public void setBooksBorrowed(ArrayList<Book> booksBorrowed){ this.booksBorrowed = booksBorrowed; }
    public void setFine(double fine){ this.fine = fine; }

    public void borrowBook(Book book) {
        booksBorrowed.add(book);
    }

    public void returnBook(Book book) {
        for (int i = 0; i < booksBorrowed.size(); i++) {
            if (booksBorrowed.get(i).getIsbn()==(book.getIsbn())) {
                booksBorrowed.remove(i);
                break;
            }
        }
    }
    public int getBorrowedBooksCount() {
        return booksBorrowed.size();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", borrowedBooks=" + booksBorrowed.size() +
                ", fine=" + fine +
                '}';
    }
}
