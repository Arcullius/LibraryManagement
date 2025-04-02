import java.util.ArrayList;
public class User{   
    //user variables
    private int id;
    private String name;
    private ArrayList<Book> booksBorrowed;

    //constructors
    public User(int id, String name){
        this.id = id;
        this.name = name;
        this.booksBorrowed = new ArrayList<>();
    }
    public User(int id, String name,ArrayList<Book> booksBorrowed){
        this.id = id;
        this.name = name;
        this.booksBorrowed = booksBorrowed;
    }

    //get methods
    public int getId() { return id; }
    public String getName() { return name; }
    public ArrayList<Book> getBooksBorrowed() { return booksBorrowed; }

    //set methods
    public void setId(int id){ this.id = id; }
    public void setName(String name){ this.name = name; }
}
