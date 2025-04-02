import java.util.ArrayList;
public class User{   
    //user variables
    private int id;
    private String name;
    private int fine;
    private ArrayList<Book> booksBorrowed;

    //constructor
    public User(int id, String name){
        this.id = id;
        this.name = name;
        this.fine = 0;
        this.booksBorrowed = new ArrayList<>();
    }

    //get methods
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public ArrayList<Book> getBooksBorrowed() { return this.booksBorrowed; }
    public int getFine() { return this.fine; }

    //set methods
    public void setId(int id){ this.id = id; }
    public void setName(String name){ this.name = name; }
    public void setBooksBorrowed(ArrayList<Book> booksBorrowed){ this.booksBorrowed = booksBorrowed; }
    public void setFine(int fine){ this.fine = fine; }
}
