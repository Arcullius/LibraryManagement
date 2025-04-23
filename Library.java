import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Library {
    public static FileManager db= new FileManager();
    private User currentUser;
    private Scanner scanner;

    public Library() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else if (currentUser.isAdmin()) {
                showAdminMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n=== Library Management System ===");
        System.out.println("1. Login");
        System.out.println("2. Create Account");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                createAccount();
                break;
            case 3:
                System.out.println("Goodbye!");
                db.closeConnection();
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    private void showAdminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Add User");
            System.out.println("4. Remove User");
            System.out.println("5. View All Transactions");
            System.out.println("6. View All Books");
            System.out.println("7. View All Users");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1: addBook(); break;
                case 2: removeBook(); break;
                case 3: addUser(); break;
                case 4: removeUser(); break;
                case 5: viewAllTransactions(); break;
                case 6: viewAllBooks(); break;
                case 7: viewAllUsers(); break;
                case 8: 
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private void showUserMenu() {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View Available Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. View My Borrowed Books");
            System.out.println("5. View My Fines");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1: viewAvailableBooks(); break;
                case 2: borrowBook(); break;
                case 3: returnBook(); break;
                case 4: viewMyBorrowedBooks(); break;
                case 5: viewMyFines(); break;
                case 6:
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        currentUser = User.login(username, password);
        if (currentUser == null) {
            System.out.println("Invalid credentials!");
        } else {
            System.out.println("Welcome, " + currentUser.getName() + "!");
        }
    }

    private void createAccount() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter first name: ");
        String fname = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lname = scanner.nextLine();

        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                "INSERT INTO User (username, password, fname, lname, isAdmin) VALUES (?, ?, ?, ?, false)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fname);
            ps.setString(4, lname);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Account created successfully!");
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private void addBook() {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        scanner.nextLine();
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter number of copies: ");
        int copies = scanner.nextInt();

        Book book = new Book(isbn, title, author, copies);
        book.save();
        System.out.println("Book added successfully!");
    }

    private void removeBook() {
        System.out.print("Enter ISBN of book to remove: ");
        String isbn = scanner.nextLine();
        Book.deleteBook(isbn);
        System.out.println("Book removed successfully!");
    }

    private void addUser() {
        createAccount();
    }

    private void removeUser() {
        System.out.print("Enter user ID to remove: ");
        int userId = scanner.nextInt();
        User.deleteUser(userId);
        System.out.println("User removed successfully!");
    }

    private void viewAllTransactions() {
        try {
            String sql = "SELECT * FROM Transactions t JOIN User u ON t.userId = u.id JOIN Book b ON t.bookIsbn = b.isbn";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Transaction ID: " + rs.getInt("transactionId"));
                System.out.println("User: " + rs.getString("fname") + " " + rs.getString("lname"));
                System.out.println("Book: " + rs.getString("title"));
                System.out.println("Borrow Date: " + rs.getDate("borrowDate"));
                System.out.println("Return Date: " + rs.getDate("returnDate"));
                System.out.println("Fine: $" + rs.getDouble("fine"));
                System.out.println("------------------------");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error viewing transactions: " + e.getMessage());
        }
    }

    private void viewAllBooks() {
        try {
            String sql = "SELECT * FROM Book";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("ISBN: " + rs.getString("isbn"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Available: " + rs.getBoolean("isAvailable"));
                System.out.println("Copies: " + rs.getInt("copies"));
                System.out.println("------------------------");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error viewing books: " + e.getMessage());
        }
    }

    private void viewAllUsers() {
        try {
            String sql = "SELECT * FROM User WHERE isAdmin = false";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("fname") + " " + rs.getString("lname"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Fine: $" + rs.getDouble("fine"));
                System.out.println("------------------------");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error viewing users: " + e.getMessage());
        }
    }

    private void viewAvailableBooks() {
        try {
            String sql = "SELECT * FROM Book WHERE isAvailable = true";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("ISBN: " + rs.getString("isbn"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Copies Available: " + rs.getInt("copies"));
                System.out.println("------------------------");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error viewing available books: " + e.getMessage());
        }
    }

    private void borrowBook() {
        System.out.print("Enter ISBN of book to borrow: ");
        String isbn = scanner.nextLine();
        
        try {
            // Check if book is available
            String checkSql = "SELECT * FROM Book WHERE isbn = ? AND isAvailable = true";
            PreparedStatement checkPs = db.getConnection().prepareStatement(checkSql);
            checkPs.setString(1, isbn);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                // Create transaction
                String transactionSql = "INSERT INTO Transactions (userId, bookIsbn, borrowDate) VALUES (?, ?, ?)";
                PreparedStatement transPs = db.getConnection().prepareStatement(transactionSql);
                transPs.setInt(1, currentUser.getId());
                transPs.setString(2, isbn);
                transPs.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                transPs.executeUpdate();

                // Update book availability
                String updateSql = "UPDATE Book SET copies = copies - 1, isAvailable = (copies > 1) WHERE isbn = ?";
                PreparedStatement updatePs = db.getConnection().prepareStatement(updateSql);
                updatePs.setString(1, isbn);
                updatePs.executeUpdate();

                System.out.println("Book borrowed successfully!");
            } else {
                System.out.println("Book is not available!");
            }
        } catch (SQLException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.print("Enter ISBN of book to return: ");
        String isbn = scanner.nextLine();
        
        try {
            // Find the transaction
            String findSql = "SELECT * FROM Transactions WHERE userId = ? AND bookIsbn = ? AND returnDate IS NULL";
            PreparedStatement findPs = db.getConnection().prepareStatement(findSql);
            findPs.setInt(1, currentUser.getId());
            findPs.setString(2, isbn);
            ResultSet rs = findPs.executeQuery();
            
            if (rs.next()) {
                int transactionId = rs.getInt("transactionId");
                LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
                LocalDate returnDate = LocalDate.now();
                
                // Calculate fine
                long daysLate = returnDate.toEpochDay() - borrowDate.toEpochDay() - 14;
                double fine = Math.max(0, daysLate);

                // Update transaction
                String updateTransSql = "UPDATE Transactions SET returnDate = ?, fine = ? WHERE transactionId = ?";
                PreparedStatement updateTransPs = db.getConnection().prepareStatement(updateTransSql);
                updateTransPs.setDate(1, java.sql.Date.valueOf(returnDate));
                updateTransPs.setDouble(2, fine);
                updateTransPs.setInt(3, transactionId);
                updateTransPs.executeUpdate();

                // Update book availability
                String updateBookSql = "UPDATE Book SET copies = copies + 1, isAvailable = true WHERE isbn = ?";
                PreparedStatement updateBookPs = db.getConnection().prepareStatement(updateBookSql);
                updateBookPs.setString(1, isbn);
                updateBookPs.executeUpdate();

                // Update user fine
                String updateUserSql = "UPDATE User SET fine = fine + ? WHERE id = ?";
                PreparedStatement updateUserPs = db.getConnection().prepareStatement(updateUserSql);
                updateUserPs.setDouble(1, fine);
                updateUserPs.setInt(2, currentUser.getId());
                updateUserPs.executeUpdate();

                System.out.println("Book returned successfully!");
                if (fine > 0) {
                    System.out.println("Late fee charged: $" + fine);
                }
            } else {
                System.out.println("No active borrowing found for this book!");
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    private void viewMyBorrowedBooks() {
        try {
            String sql = "SELECT b.*, t.borrowDate FROM Book b " +
                        "JOIN Transactions t ON b.isbn = t.bookIsbn " +
                        "WHERE t.userId = ? AND t.returnDate IS NULL";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setInt(1, currentUser.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("ISBN: " + rs.getString("isbn"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Borrowed Date: " + rs.getDate("borrowDate"));
                System.out.println("Due Date: " + rs.getDate("borrowDate").toLocalDate().plusDays(14));
                System.out.println("------------------------");
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error viewing borrowed books: " + e.getMessage());
        }
    }

    private void viewMyFines() {
        try {
            String sql = "SELECT fine FROM User WHERE id = ?";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setInt(1, currentUser.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double fine = rs.getDouble("fine");
                System.out.println("Your current fine balance: $" + fine);
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error viewing fines: " + e.getMessage());
        }
    }
    }



