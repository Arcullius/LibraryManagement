import java.sql.*;

public class BookSQL {
    private int isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    private int copies;
    private static FileManager db;

    public BookSQL(int isbn, String title, String author, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.isAvailable = copies > 0;
        
        if (db == null) {
            db = new FileManager();
        }
    }

    // Getters
    public int getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public int getCopies() { return copies; }

    // Setters
    public void setIsbn(int isbn) { 
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
            String sql = "INSERT OR REPLACE INTO Book (isbn, title, author, isAvailable, copies) VALUES (?, ?, ?, ?, ?)";
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBook() {
        try {
            String sql = "UPDATE Book SET title = ?, author = ?, isAvailable = ?, copies = ? WHERE isbn = ?";
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Book getBook(int isbn) {
        try {
            String sql = "SELECT * FROM Book WHERE isbn = ?";
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteBook(int isbn) {
        try {
            String sql = "DELETE FROM Book WHERE isbn = ?";
            
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
