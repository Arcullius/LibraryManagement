import java.sql.*;

public class Book implements IBook {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    private int copies;
    private String homeLibrary;
    
   

    public Book(String isbn, String title, String author, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.isAvailable = copies > 0;
        this.homeLibrary = "LOCAL"; // Default to local library
    }

    public Book(String isbn, String title, String author, int copies, String homeLibrary) {
        this(isbn, title, author, copies);
        this.homeLibrary = homeLibrary;
    }

    // IBook interface implementation
    @Override
    public String getTitle() { return title; }
    
    @Override
    public String getAuthor() { return author; }
    
    @Override
    public String getISBN() { return isbn; }
    
    @Override
    public String getHomeLibrary() { return homeLibrary; }

    // Getters
    public boolean isAvailable() { return isAvailable; }
    public int getCopies() { return copies; }

    // Setters with database updates
    public void setIsbn(String isbn) { 
        this.isbn = isbn;
        updateBook();
    }
    
    public void setTitle(String title) {
        this.title = title;
        updateBook();
    }
    
    public void setAuthor(String author) {
        this.author = author;
        updateBook();
    }
    
    public void setCopies(int copies) {
        this.copies = copies;
        this.isAvailable = copies > 0;
        updateBook();
    }

    public void setHomeLibrary(String homeLibrary) {
        this.homeLibrary = homeLibrary;
        updateBook();
    }

    // Database operations
    public void save() {
        try {
            // First check if book exists
            String checkSql = "SELECT copies FROM Book WHERE isbn = ?";
            PreparedStatement checkPs = Library.db.getConnection().prepareStatement(checkSql);
            checkPs.setString(1, isbn);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                // Book exists, update copies
                int existingCopies = rs.getInt("copies");
                String updateSql = "UPDATE Book SET copies = copies + ?, homeLibrary = ? WHERE isbn = ?";
                PreparedStatement updatePs = Library.db.getConnection().prepareStatement(updateSql);
                updatePs.setInt(1, copies);
                updatePs.setString(2, homeLibrary);
                updatePs.setString(3, isbn);
                updatePs.executeUpdate();
                updatePs.close();
            } else {
                // Book doesn't exist, insert new
                String insertSql = "INSERT INTO Book (isbn, title, author, isAvailable, copies, homeLibrary) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertPs = Library.db.getConnection().prepareStatement(insertSql);
                insertPs.setString(1, isbn);
                insertPs.setString(2, title);
                insertPs.setString(3, author);
                insertPs.setBoolean(4, isAvailable);
                insertPs.setInt(5, copies);
                insertPs.setString(6, homeLibrary);
                insertPs.executeUpdate();
                insertPs.close();
            }
            checkPs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBook() {
        try {
            String sql = "UPDATE Book SET title = ?, author = ?, isAvailable = ?, copies = ?, homeLibrary = ? WHERE isbn = ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setBoolean(3, isAvailable);
            ps.setInt(4, copies);
            ps.setString(5, homeLibrary);
            ps.setString(6, isbn);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Book getBook(String isbn) {
        try {
            String sql = "SELECT * FROM Book WHERE isbn = ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Book book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("copies"),
                    rs.getString("homeLibrary")
                );
                ps.close();
                return book;
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteBook(String isbn) {
        try {
            String sql = "DELETE FROM Book WHERE isbn = ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setString(1, isbn);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(String title, String author, int copies) {
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.isAvailable = copies > 0;
        updateBook();
    }

    @Override
    public String toString() {
        return "Book: {" +
                "ISBN='" + isbn + '\'' +
                ", Title='" + title + '\'' +
                ", Author='" + author + '\'' +
                ", Availability=" + isAvailable +
                ", Copies=" + copies +
                ", HomeLibrary='" + homeLibrary + '\'' +
                '}';
    }
}
