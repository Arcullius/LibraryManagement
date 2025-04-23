import java.util.ArrayList;
import java.sql.*;

public class User {   
    //user variables
    private int id;
    private String username;
    private String password;
    private String fname;
    private String lname;
    private boolean isAdmin;
    private double fine;
    private ArrayList<Book> booksBorrowed;
    

    //constructor
    public User(int id, String username, String password, String fname, String lname, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.isAdmin = isAdmin;
        this.fine = 0;
        this.booksBorrowed = new ArrayList<>();
        
        
    }

    //get methods
    public int getId() { return id; }
    public String getFirstName() { return fname; }
    public String getLastName() { return lname; }
    public String getName() { return fname + " " + lname; }
    public ArrayList<Book> getBooksBorrowed() { return booksBorrowed; }
    public double getFine() { return fine; }
    public String getUsername() { return username; }
    public boolean isAdmin() { return isAdmin; }

    //set methods
    public void setId(int id){ this.id = id; }
    public void setFirstName(String name){ this.fname = name; }
    public void setLastName(String name){ this.lname = name; }
    public void setBooksBorrowed(ArrayList<Book> booksBorrowed){ this.booksBorrowed = booksBorrowed; }
    public void setFine(double fine){ this.fine = fine; }

    //adds a book to the user's borrowed books
    public void borrowBook(Book book) {
        booksBorrowed.add(book);
    }

    //removes book from the user's borrowed books
    public void returnBook(Book book) {
        for (int i = 0; i < booksBorrowed.size(); i++) {
            if (booksBorrowed.get(i).getIsbn()==(book.getIsbn())) {
                booksBorrowed.remove(i);
                break;
            }
        }
    }

    //Returns the number of books a user has borrowed
    public int getBorrowedBooksCount() {
        return booksBorrowed.size();
    }

    //toString method
    @Override
    public String toString() {
        return "User: {" +
                "id='" + id + '\'' +
                ", name='" + getName() + '\'' +
                ", borrowedBooks=" + booksBorrowed.size() +
                ", fine=" + fine +
                '}';
    }

    public static User login(String username, String password) {
        try {
            String sql = "SELECT * FROM User WHERE username = ? AND password = ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getBoolean("isAdmin")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save() {
        try {
            String sql = "INSERT OR REPLACE INTO User (id, username, password, fname, lname, isAdmin, fine) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, fname);
            ps.setString(5, lname);
            ps.setBoolean(6, isAdmin);
            ps.setDouble(7, fine);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(int userId) {
        try {
            String sql = "DELETE FROM User WHERE id = ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
