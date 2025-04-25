import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Statistics {
    // Get most borrowed books
    public static List<Map<String, Object>> getMostBorrowedBooks(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            String sql = "SELECT b.isbn, b.title, b.author, COUNT(t.transactionId) as borrowCount " +
                        "FROM Book b JOIN Transactions t ON b.isbn = t.bookIsbn " +
                        "GROUP BY b.isbn, b.title, b.author " +
                        "ORDER BY borrowCount DESC LIMIT ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> bookStats = new HashMap<>();
                bookStats.put("isbn", rs.getString("isbn"));
                bookStats.put("title", rs.getString("title"));
                bookStats.put("author", rs.getString("author"));
                bookStats.put("borrowCount", rs.getInt("borrowCount"));
                results.add(bookStats);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get hardest to get books (most reservations when unavailable)
    public static List<Map<String, Object>> getHardestToGetBooks(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            String sql = "SELECT b.isbn, b.title, b.author, COUNT(r.reservationId) as reservationCount " +
                        "FROM Book b JOIN Reservations r ON b.isbn = r.bookIsbn " +
                        "WHERE b.copies = 0 " +
                        "GROUP BY b.isbn, b.title, b.author " +
                        "ORDER BY reservationCount DESC LIMIT ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> bookStats = new HashMap<>();
                bookStats.put("isbn", rs.getString("isbn"));
                bookStats.put("title", rs.getString("title"));
                bookStats.put("author", rs.getString("author"));
                bookStats.put("reservationCount", rs.getInt("reservationCount"));
                results.add(bookStats);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get never borrowed books
    public static List<Map<String, Object>> getNeverBorrowedBooks() {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            String sql = "SELECT b.isbn, b.title, b.author, b.copies " +
                        "FROM Book b LEFT JOIN Transactions t ON b.isbn = t.bookIsbn " +
                        "WHERE t.transactionId IS NULL";
            Statement stmt = Library.db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> bookStats = new HashMap<>();
                bookStats.put("isbn", rs.getString("isbn"));
                bookStats.put("title", rs.getString("title"));
                bookStats.put("author", rs.getString("author"));
                bookStats.put("copies", rs.getInt("copies"));
                results.add(bookStats);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get most active users
    public static List<Map<String, Object>> getMostActiveUsers(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            String sql = "SELECT u.id, u.fname, u.lname, COUNT(t.transactionId) as borrowCount " +
                        "FROM User u JOIN Transactions t ON u.id = t.userId " +
                        "GROUP BY u.id, u.fname, u.lname " +
                        "ORDER BY borrowCount DESC LIMIT ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> userStats = new HashMap<>();
                userStats.put("id", rs.getInt("id"));
                userStats.put("name", rs.getString("fname") + " " + rs.getString("lname"));
                userStats.put("borrowCount", rs.getInt("borrowCount"));
                results.add(userStats);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get books with highest overdue rate
    public static List<Map<String, Object>> getBooksWithHighestOverdueRate(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            String sql = "SELECT b.isbn, b.title, b.author, " +
                        "COUNT(CASE WHEN t.fine > 0 THEN 1 END) as overdueCount, " +
                        "COUNT(t.transactionId) as totalBorrows, " +
                        "ROUND(CAST(COUNT(CASE WHEN t.fine > 0 THEN 1 END) AS FLOAT) / COUNT(t.transactionId) * 100, 2) as overdueRate " +
                        "FROM Book b JOIN Transactions t ON b.isbn = t.bookIsbn " +
                        "GROUP BY b.isbn, b.title, b.author " +
                        "HAVING COUNT(t.transactionId) > 0 " +
                        "ORDER BY overdueRate DESC LIMIT ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> bookStats = new HashMap<>();
                bookStats.put("isbn", rs.getString("isbn"));
                bookStats.put("title", rs.getString("title"));
                bookStats.put("author", rs.getString("author"));
                bookStats.put("overdueCount", rs.getInt("overdueCount"));
                bookStats.put("totalBorrows", rs.getInt("totalBorrows"));
                bookStats.put("overdueRate", rs.getDouble("overdueRate"));
                results.add(bookStats);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get monthly borrowing trends
    public static List<Map<String, Object>> getMonthlyBorrowingTrends() {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            String sql = "SELECT " +
                        "EXTRACT(YEAR FROM borrowDate) as year, " +
                        "EXTRACT(MONTH FROM borrowDate) as month, " +
                        "COUNT(transactionId) as borrowCount " +
                        "FROM Transactions " +
                        "GROUP BY EXTRACT(YEAR FROM borrowDate), EXTRACT(MONTH FROM borrowDate) " +
                        "ORDER BY year, month";
            Statement stmt = Library.db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> trend = new HashMap<>();
                trend.put("year", rs.getInt("year"));
                trend.put("month", rs.getInt("month"));
                trend.put("borrowCount", rs.getInt("borrowCount"));
                results.add(trend);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
} 