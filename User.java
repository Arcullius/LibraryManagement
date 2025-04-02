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
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public ArrayList<Book> getBooksBorrowed() { return this.booksBorrowed; }
    public double getFine() { return this.fine; }

    //set methods
    public void setId(int id){ this.id = id; }
    public void setName(String name){ this.name = name; }
    public void setBooksBorrowed(ArrayList<Book> booksBorrowed){ this.booksBorrowed = booksBorrowed; }
    public void setFine(double fine){ this.fine = fine; }
}
