import java.sql.*;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    private int copies;
    
   

    public Book(String isbn, String title, String author, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.isAvailable = copies > 0;
        
        
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
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
                String updateSql = "UPDATE Book SET copies = copies + ? WHERE isbn = ?";
                PreparedStatement updatePs = Library.db.getConnection().prepareStatement(updateSql);
                updatePs.setInt(1, copies);
                updatePs.setString(2, isbn);
                updatePs.executeUpdate();
                updatePs.close();
            } else {
                // Book doesn't exist, insert new
                String insertSql = "INSERT INTO Book (isbn, title, author, isAvailable, copies) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertPs = Library.db.getConnection().prepareStatement(insertSql);
                insertPs.setString(1, isbn);
                insertPs.setString(2, title);
                insertPs.setString(3, author);
                insertPs.setBoolean(4, isAvailable);
                insertPs.setInt(5, copies);
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
            String sql = "UPDATE Book SET title = ?, author = ?, isAvailable = ?, copies = ? WHERE isbn = ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setBoolean(3, isAvailable);
            ps.setInt(4, copies);
            ps.setString(5, isbn);
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
                    rs.getInt("copies")
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

    @Override
    public String toString() {
        return "Book: {" +
                "ISBN='" + isbn + '\'' +
                ", Title='" + title + '\'' +
                ", Author='" + author + '\'' +
                ", Availability=" + isAvailable +
                ", Copies=" + copies +
                '}';
    }
}
