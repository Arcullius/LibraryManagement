import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private int reservationId;
    private int userId;
    private String bookIsbn;
    private LocalDate reservationDate;
    private LocalDate notificationDate;
    private boolean isNotified;
    private boolean isActive;

    public Reservation(int userId, String bookIsbn) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.reservationDate = LocalDate.now();
        this.isNotified = false;
        this.isActive = true;
    }

    // Getters
    public int getReservationId() { return reservationId; }
    public int getUserId() { return userId; }
    public String getBookIsbn() { return bookIsbn; }
    public LocalDate getReservationDate() { return reservationDate; }
    public LocalDate getNotificationDate() { return notificationDate; }
    public boolean isNotified() { return isNotified; }
    public boolean isActive() { return isActive; }

    // Save reservation to database
    public void save() {
        try {
            String sql = "INSERT INTO Reservations (userId, bookIsbn, reservationDate, isNotified, isActive) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setString(2, bookIsbn);
            ps.setDate(3, java.sql.Date.valueOf(reservationDate));
            ps.setBoolean(4, isNotified);
            ps.setBoolean(5, isActive);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.reservationId = rs.getInt(1);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update reservation status
    public void updateStatus(boolean isActive) {
        try {
            String sql = "UPDATE Reservations SET isActive = ? WHERE reservationId = ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setBoolean(1, isActive);
            ps.setInt(2, reservationId);
            ps.executeUpdate();
            ps.close();
            this.isActive = isActive;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mark reservation as notified
    public void markAsNotified() {
        try {
            String sql = "UPDATE Reservations SET isNotified = true, notificationDate = ? WHERE reservationId = ?";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(2, reservationId);
            ps.executeUpdate();
            ps.close();
            this.isNotified = true;
            this.notificationDate = LocalDate.now();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all active reservations for a book
    public static List<Reservation> getActiveReservations(String bookIsbn) {
        List<Reservation> reservations = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Reservations WHERE bookIsbn = ? AND isActive = true ORDER BY reservationDate";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setString(1, bookIsbn);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation(rs.getInt("userId"), bookIsbn);
                reservation.reservationId = rs.getInt("reservationId");
                reservation.reservationDate = rs.getDate("reservationDate").toLocalDate();
                reservation.isNotified = rs.getBoolean("isNotified");
                if (rs.getDate("notificationDate") != null) {
                    reservation.notificationDate = rs.getDate("notificationDate").toLocalDate();
                }
                reservations.add(reservation);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // Get all reservations for a user
    public static List<Reservation> getUserReservations(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Reservations WHERE userId = ? ORDER BY reservationDate DESC";
            PreparedStatement ps = Library.db.getConnection().prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation(userId, rs.getString("bookIsbn"));
                reservation.reservationId = rs.getInt("reservationId");
                reservation.reservationDate = rs.getDate("reservationDate").toLocalDate();
                reservation.isNotified = rs.getBoolean("isNotified");
                reservation.isActive = rs.getBoolean("isActive");
                if (rs.getDate("notificationDate") != null) {
                    reservation.notificationDate = rs.getDate("notificationDate").toLocalDate();
                }
                reservations.add(reservation);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
} 