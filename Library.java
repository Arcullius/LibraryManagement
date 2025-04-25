import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
        clearConsole();
        System.out.println("=== Library Management System ===");
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
                promptEnter();
        }
    }

    private void showAdminMenu() {
        while (true) {
            clearConsole();
            System.out.println("=== Admin Menu ===");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Add User");
            System.out.println("4. Remove User");
            System.out.println("5. View All Transactions");
            System.out.println("6. View All Books");
            System.out.println("7. View All Users");
            System.out.println("8. Mass Upload Books (CSV)");
            System.out.println("9. Mass Upload Users (CSV)");
            System.out.println("10. Update Book");
            System.out.println("11. Update User");
            System.out.println("12. Logout");
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
                case 8: massUploadBooks(); break;
                case 9: massUploadUsers(); break;
                case 10: updateBook(); break;
                case 11: updateUser(); break;
                case 12: 
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid option!");
                    promptEnter();
            }
        }
    }

    private void showUserMenu() {
        while (true) {
            clearConsole();
            System.out.println("=== User Menu ===");
            System.out.println("1. Search Book");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. View My Borrowed Books");
            System.out.println("5. View My Fines");
            System.out.println("6. Pay Fine");
            System.out.println("7. Update My Profile");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1: searchBook(); break;
                case 2: borrowBook(); break;
                case 3: returnBook(); break;
                case 4: viewMyBorrowedBooks(); break;
                case 5: viewMyFines(); break;
                case 6: payFine(); break;
                case 7: updateMyProfile(); break;
                case 8:
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid option!");
                    promptEnter();
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
            promptEnter();
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
        promptEnter();
    }

    private void addBook() {
        clearConsole();
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
        promptEnter();
    }

    private void removeBook() {
        clearConsole();
        System.out.println("=== Remove Book ===");
        
        // First show all books
        try {
            String sql = "SELECT * FROM Book ORDER BY title";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\nAvailable Books:");
            System.out.println("ID | ISBN | Title | Author | Copies");
            System.out.println("----------------------------------------");
            
            int index = 1;
            while (rs.next()) {
                System.out.printf("%d. %s | %s | %s | %d copies%n",
                    index++,
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("copies")
                );
            }
            stmt.close();
            
            System.out.print("\nEnter the number of the book to remove (or 0 to cancel): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            if (choice == 0) {
                System.out.println("Operation cancelled.");
                promptEnter();
                return;
            }
            
            // Get the selected book's ISBN
            stmt = db.getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            int currentIndex = 1;
            String selectedIsbn = null;
            String selectedTitle = null;
            int currentCopies = 0;
            
            while (rs.next() && currentIndex <= choice) {
                if (currentIndex == choice) {
                    selectedIsbn = rs.getString("isbn");
                    selectedTitle = rs.getString("title");
                    currentCopies = rs.getInt("copies");
                    break;
                }
                currentIndex++;
            }
            stmt.close();
            
            if (selectedIsbn == null) {
                System.out.println("Invalid selection.");
                promptEnter();
                return;
            }
            
            System.out.println("\nSelected book: " + selectedTitle);
            System.out.println("Current number of copies: " + currentCopies);
            System.out.print("Enter number of copies to remove (or 'all' to remove all copies): ");
            
            String input = scanner.nextLine().trim();
            int copiesToRemove;
            
            if (input.equalsIgnoreCase("all")) {
                copiesToRemove = currentCopies;
            } else {
                try {
                    copiesToRemove = Integer.parseInt(input);
                    if (copiesToRemove <= 0) {
                        System.out.println("Number of copies must be positive.");
                        promptEnter();
                        return;
                    }
                    if (copiesToRemove > currentCopies) {
                        System.out.println("Cannot remove more copies than available.");
                        promptEnter();
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number or 'all'.");
                    promptEnter();
                    return;
                }
            }
            
            if (copiesToRemove == currentCopies) {
                // Remove all copies - delete the book
                Book.deleteBook(selectedIsbn);
                System.out.println("Book '" + selectedTitle + "' has been completely removed from the library.");
            } else {
                // Update copies
                String updateSql = "UPDATE Book SET copies = copies - ? WHERE isbn = ?";
                PreparedStatement updatePs = db.getConnection().prepareStatement(updateSql);
                updatePs.setInt(1, copiesToRemove);
                updatePs.setString(2, selectedIsbn);
                updatePs.executeUpdate();
                updatePs.close();
                
                System.out.println("Removed " + copiesToRemove + " copies of '" + selectedTitle + "'.");
                System.out.println("Remaining copies: " + (currentCopies - copiesToRemove));
            }
        } catch (SQLException e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
        promptEnter();
    }

    private void addUser() {
        createAccount();
    }

    private void removeUser() {
        clearConsole();
        System.out.println("=== Remove User ===");
        
        // First show all users
        try {
            String sql = "SELECT * FROM User WHERE isAdmin = false ORDER BY fname, lname";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\nAvailable Users:");
            System.out.println("ID | Name | Username");
            System.out.println("----------------------------------------");
            
            int index = 1;
            while (rs.next()) {
                System.out.printf("%d. %s %s | %s%n",
                    index++,
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("username")
                );
            }
            stmt.close();
            
            System.out.print("\nEnter the number of the user to remove (or 0 to cancel): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            if (choice == 0) {
                System.out.println("Operation cancelled.");
                promptEnter();
                return;
            }
            
            // Get the selected user's ID
            stmt = db.getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            int currentIndex = 1;
            int selectedId = -1;
            String selectedName = null;
            
            while (rs.next() && currentIndex <= choice) {
                if (currentIndex == choice) {
                    selectedId = rs.getInt("id");
                    selectedName = rs.getString("fname") + " " + rs.getString("lname");
                    break;
                }
                currentIndex++;
            }
            stmt.close();
            
            if (selectedId == -1) {
                System.out.println("Invalid selection.");
                promptEnter();
                return;
            }
            
            // Confirm deletion
            System.out.print("\nAre you sure you want to remove user '" + selectedName + "'? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("yes")) {
                User.deleteUser(selectedId);
                System.out.println("User '" + selectedName + "' has been removed successfully.");
            } else {
                System.out.println("Operation cancelled.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing user: " + e.getMessage());
        }
        promptEnter();
    }

    private void viewAllTransactions() {
        try {
            String sql = "SELECT * FROM Transactions t JOIN User u ON t.userId = u.id JOIN Book b ON t.bookIsbn = b.isbn";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            clearConsole();
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
        promptEnter();
    }

    private void viewAllBooks() {
        try {
            String sql = "SELECT * FROM Book";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            clearConsole();
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
        promptEnter();
    }

    private void viewAllUsers() {
        try {
            String sql = "SELECT * FROM User WHERE isAdmin = false";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            clearConsole();
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
        promptEnter();
    }

    private void searchBook() {
        System.out.println("Search by:");
        System.out.println("1. ISBN");
        System.out.println("2. Title");
        System.out.println("3. Author");
        System.out.println("4. View All Available Books");
        System.out.print("Enter choice (1, 2, 3, or 4): ");
        String choice = scanner.nextLine();

        String searchSql;
        String searchValue;

        try {
            if (choice.equals("1")) {
                System.out.print("Enter ISBN of book: ");
                searchValue = scanner.nextLine();
                searchSql = "SELECT * FROM Book WHERE isbn = ? AND isAvailable = true";

                PreparedStatement checkPs = db.getConnection().prepareStatement(searchSql);
                checkPs.setString(1, searchValue);
                ResultSet rs = checkPs.executeQuery();

                List<String> isbns = new ArrayList<>();
                int index = 1;
                while (rs.next()) {
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    int copies = rs.getInt("copies");

                    System.out.printf("%d. %s (ISBN: %s, Copies: %d)%n", index++, title, isbn, copies);
                    isbns.add(isbn);
                }

                if (isbns.isEmpty()) {
                    System.out.println("No books found with that isbn.");
                    promptEnter();
                    return;
                }
            } 
            else if (choice.equals("2")) {
                System.out.print("Enter title of book: ");
                searchValue = scanner.nextLine();
                searchSql = "SELECT * FROM Book WHERE title LIKE ? AND isAvailable = true";

                PreparedStatement checkPs = db.getConnection().prepareStatement(searchSql);
                checkPs.setString(1, "%" + searchValue + "%");
                ResultSet rs = checkPs.executeQuery();

                List<String> isbns = new ArrayList<>();
                int index = 1;
                while (rs.next()) {
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    int copies = rs.getInt("copies");

                    System.out.printf("%d. %s (ISBN: %s, Copies: %d)%n", index++, title, isbn, copies);
                    isbns.add(isbn);
                }

                if (isbns.isEmpty()) {
                    System.out.println("No books found matching that title.");
                    promptEnter();
                    return;
                }
            }
            else if (choice.equals("3")) {
                System.out.print("Enter name of the author: ");
                searchValue = scanner.nextLine();
                searchSql = "SELECT * FROM Book WHERE author LIKE ? AND isAvailable = true";

                PreparedStatement checkPs = db.getConnection().prepareStatement(searchSql);
                checkPs.setString(1, "%" + searchValue + "%");
                ResultSet rs = checkPs.executeQuery();

                List<String> isbns = new ArrayList<>();
                int index = 1;
                while (rs.next()) {
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    int copies = rs.getInt("copies");

                    System.out.printf("%d. %s (ISBN: %s, Copies: %d)%n", index++, title, isbn, copies);
                    isbns.add(isbn);
                }

                if (isbns.isEmpty()) {
                    System.out.println("No books found with that author.");
                    promptEnter();
                    return;
                }
            }
            else if (choice.equals("4")) {
                // Show all available books
                searchSql = "SELECT * FROM Book WHERE isAvailable = true ORDER BY title";
                Statement stmt = db.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(searchSql);

                List<String> isbns = new ArrayList<>();
                int index = 1;
                
                System.out.println("\nAvailable Books:");
                System.out.println("ID | Title | Author | Copies");
                System.out.println("----------------------------------------");
                
                while (rs.next()) {
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    String author = rs.getString("author");
                    int copies = rs.getInt("copies");

                    System.out.printf("%d. %s | %s | %d copies%n", 
                        index++, 
                        title, 
                        author,
                        copies
                    );
                    isbns.add(isbn);
                }

                if (isbns.isEmpty()) {
                    System.out.println("No books are currently available.");
                    promptEnter();
                    return;
                }
            }
            else {
                System.out.println("Invalid choice.");
            }
        } 
        catch (SQLException | NumberFormatException e) {
            System.out.println("Error viewing book: " + e.getMessage());
        }
        promptEnter();
    }

    private void borrowBookByISBN(String isbn) throws SQLException {
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
    }

    private void borrowBook() {
        System.out.println("Search by:");
        System.out.println("1. ISBN");
        System.out.println("2. Title");
        System.out.println("3. Author");
        System.out.println("4. View All Available Books");
        System.out.print("Enter choice (1, 2, 3, or 4): ");
        String choice = scanner.nextLine();

        String searchSql;
        String searchValue;

        try {
            if (choice.equals("1")) {
                System.out.print("Enter ISBN of book to borrow: ");
                searchValue = scanner.nextLine();
                searchSql = "SELECT * FROM Book WHERE isbn = ? AND isAvailable = true";

                PreparedStatement checkPs = db.getConnection().prepareStatement(searchSql);
                checkPs.setString(1, searchValue);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    borrowBookByISBN(rs.getString("isbn"));
                } else {
                    System.out.println("Book is not available or doesn't exist.");
                }
            } 
            else if (choice.equals("2")) {
                System.out.print("Enter title of book to borrow: ");
                searchValue = scanner.nextLine();
                searchSql = "SELECT * FROM Book WHERE title LIKE ? AND isAvailable = true";

                PreparedStatement checkPs = db.getConnection().prepareStatement(searchSql);
                checkPs.setString(1, "%" + searchValue + "%");
                ResultSet rs = checkPs.executeQuery();

                List<String> isbns = new ArrayList<>();
                int index = 1;
                while (rs.next()) {
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    int copies = rs.getInt("copies");

                    System.out.printf("%d. %s (ISBN: %s, Copies: %d)%n", index++, title, isbn, copies);
                    isbns.add(isbn);
                }

                if (isbns.isEmpty()) {
                    System.out.println("No books found matching that title.");
                    promptEnter();
                    return;
                }

                System.out.print("Enter the number of the book to borrow (or 0 to cancel): ");
                int choiceNum = Integer.parseInt(scanner.nextLine());

                if (choiceNum == 0) {
                    System.out.println("Canceled.");
                } else if (choiceNum > 0 && choiceNum <= isbns.size()) {
                    borrowBookByISBN(isbns.get(choiceNum - 1));
                } else {
                    System.out.println("Invalid selection.");
                }
            }
            else if (choice.equals("3")) {
                System.out.print("Enter name of the author: ");
                searchValue = scanner.nextLine();
                searchSql = "SELECT * FROM Book WHERE author LIKE ? AND isAvailable = true";

                PreparedStatement checkPs = db.getConnection().prepareStatement(searchSql);
                checkPs.setString(1, "%" + searchValue + "%");
                ResultSet rs = checkPs.executeQuery();

                List<String> isbns = new ArrayList<>();
                int index = 1;
                while (rs.next()) {
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    int copies = rs.getInt("copies");

                    System.out.printf("%d. %s (ISBN: %s, Copies: %d)%n", index++, title, isbn, copies);
                    isbns.add(isbn);
                }

                if (isbns.isEmpty()) {
                    System.out.println("No books found with that author.");
                    promptEnter();
                    return;
                }

                System.out.print("Enter the number of the book to borrow (or 0 to cancel): ");
                int choiceNum = Integer.parseInt(scanner.nextLine());

                if (choiceNum == 0) {
                    System.out.println("Canceled.");
                } else if (choiceNum > 0 && choiceNum <= isbns.size()) {
                    borrowBookByISBN(isbns.get(choiceNum - 1));
                } else {
                    System.out.println("Invalid selection.");
                }
            }
            else if (choice.equals("4")) {
                // Show all available books
                searchSql = "SELECT * FROM Book WHERE isAvailable = true ORDER BY title";
                Statement stmt = db.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(searchSql);

                List<String> isbns = new ArrayList<>();
                int index = 1;
                
                System.out.println("\nAvailable Books:");
                System.out.println("ID | Title | Author | Copies");
                System.out.println("----------------------------------------");
                
                while (rs.next()) {
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    String author = rs.getString("author");
                    int copies = rs.getInt("copies");

                    System.out.printf("%d. %s | %s | %d copies%n", 
                        index++, 
                        title, 
                        author,
                        copies
                    );
                    isbns.add(isbn);
                }

                if (isbns.isEmpty()) {
                    System.out.println("No books are currently available.");
                    promptEnter();
                    return;
                }

                System.out.print("\nEnter the number of the book to borrow (or 0 to cancel): ");
                int choiceNum = Integer.parseInt(scanner.nextLine());

                if (choiceNum == 0) {
                    System.out.println("Canceled.");
                } else if (choiceNum > 0 && choiceNum <= isbns.size()) {
                    borrowBookByISBN(isbns.get(choiceNum - 1));
                } else {
                    System.out.println("Invalid selection.");
                }
                stmt.close();
            }
            else {
                System.out.println("Invalid choice.");
            }
        } 
        catch (SQLException | NumberFormatException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
        promptEnter();
    }

    private void returnBook() {
        clearConsole();

        try {
            // Step 1: Get list of currently borrowed books by this user
            String borrowedSql = "SELECT t.transactionId, b.title, b.isbn, t.borrowDate " +
                                "FROM Transactions t " +
                                "JOIN Book b ON t.bookIsbn = b.isbn " +
                                "WHERE t.userId = ? AND t.returnDate IS NULL";
            PreparedStatement borrowedPs = db.getConnection().prepareStatement(borrowedSql);
            borrowedPs.setInt(1, currentUser.getId());
            ResultSet rs = borrowedPs.executeQuery();

            List<Integer> transactionIds = new ArrayList<>();
            List<String> isbns = new ArrayList<>();

            int index = 1;
            System.out.println("Books you have currently borrowed:");
            while (rs.next()) {
                int transactionId = rs.getInt("transactionId");
                String title = rs.getString("title");
                String isbn = rs.getString("isbn");
                LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();

                System.out.printf("%d. %s (ISBN: %s) - Borrowed on %s%n", index, title, isbn, borrowDate);
                transactionIds.add(transactionId);
                isbns.add(isbn);
                index++;
            }

            if (transactionIds.isEmpty()) {
                System.out.println("You don't have any books to return.");
                promptEnter();
                return;
            }

            // Step 2: Ask user to choose which book to return
            System.out.print("Enter the number of the book to return: ");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice < 1 || choice > transactionIds.size()) {
                System.out.println("Invalid selection.");
                promptEnter();
                return;
            }

            int transactionId = transactionIds.get(choice - 1);
            String isbn = isbns.get(choice - 1);

            // Step 3: Calculate fine
            String dateSql = "SELECT borrowDate FROM Transactions WHERE transactionId = ?";
            PreparedStatement datePs = db.getConnection().prepareStatement(dateSql);
            datePs.setInt(1, transactionId);
            ResultSet dateRs = datePs.executeQuery();
            dateRs.next();
            LocalDate borrowDate = dateRs.getDate("borrowDate").toLocalDate();
            LocalDate returnDate = LocalDate.now();
            long daysLate = returnDate.toEpochDay() - borrowDate.toEpochDay() - 14;
            double fine = Math.max(0, daysLate);

            // Step 4: Update transaction
            String updateTransSql = "UPDATE Transactions SET returnDate = ?, fine = ? WHERE transactionId = ?";
            PreparedStatement updateTransPs = db.getConnection().prepareStatement(updateTransSql);
            updateTransPs.setDate(1, java.sql.Date.valueOf(returnDate));
            updateTransPs.setDouble(2, fine);
            updateTransPs.setInt(3, transactionId);
            updateTransPs.executeUpdate();

            // Step 5: Update book availability
            String updateBookSql = "UPDATE Book SET copies = copies + 1, isAvailable = true WHERE isbn = ?";
            PreparedStatement updateBookPs = db.getConnection().prepareStatement(updateBookSql);
            updateBookPs.setString(1, isbn);
            updateBookPs.executeUpdate();

            // Step 6: Update user's fine
            String updateUserSql = "UPDATE User SET fine = fine + ? WHERE id = ?";
            PreparedStatement updateUserPs = db.getConnection().prepareStatement(updateUserSql);
            updateUserPs.setDouble(1, fine);
            updateUserPs.setInt(2, currentUser.getId());
            updateUserPs.executeUpdate();

            System.out.println("Book returned successfully!");
            if (fine > 0) {
                System.out.println("Late fee charged: $" + fine);
            }
        } 
        catch (SQLException | NumberFormatException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
        promptEnter();
    }

    private void viewMyBorrowedBooks() {
        try {
            String sql = "SELECT b.*, t.borrowDate FROM Book b " +
                        "JOIN Transactions t ON b.isbn = t.bookIsbn " +
                        "WHERE t.userId = ? AND t.returnDate IS NULL";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setInt(1, currentUser.getId());
            ResultSet rs = ps.executeQuery();
            clearConsole();
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
        promptEnter();
    }

    private void viewMyFines() {
        try {
            String sql = "SELECT fine FROM User WHERE id = ?";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setInt(1, currentUser.getId());
            ResultSet rs = ps.executeQuery();
            clearConsole();
            if (rs.next()) {
                double fine = rs.getDouble("fine");
                System.out.println("Your current fine balance: $" + fine);
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error viewing fines: " + e.getMessage());
        }
        promptEnter();
    }

    private void payFine() {
        clearConsole();
        System.out.println("=== Pay Fine ===");
        
        try {
            // Get current fine amount
            String sql = "SELECT fine FROM User WHERE id = ?";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setInt(1, currentUser.getId());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double currentFine = rs.getDouble("fine");
                
                if (currentFine <= 0) {
                    System.out.println("You don't have any outstanding fines.");
                    promptEnter();
                    return;
                }
                
                System.out.printf("Your current fine balance: $%.2f%n", currentFine);
                System.out.print("Enter amount to pay (or 'all' to pay full amount): $");
                
                String input = scanner.nextLine().trim();
                double paymentAmount;
                
                if (input.equalsIgnoreCase("all")) {
                    paymentAmount = currentFine;
                } else {
                    try {
                        paymentAmount = Double.parseDouble(input);
                        if (paymentAmount <= 0) {
                            System.out.println("Payment amount must be positive.");
                            promptEnter();
                            return;
                        }
                        if (paymentAmount > currentFine) {
                            System.out.println("Payment amount cannot exceed your fine balance.");
                            promptEnter();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number or 'all'.");
                        promptEnter();
                        return;
                    }
                }
                
                // Update fine in database
                String updateSql = "UPDATE User SET fine = fine - ? WHERE id = ?";
                PreparedStatement updatePs = db.getConnection().prepareStatement(updateSql);
                updatePs.setDouble(1, paymentAmount);
                updatePs.setInt(2, currentUser.getId());
                updatePs.executeUpdate();
                updatePs.close();
                
                // Get updated fine amount
                ps = db.getConnection().prepareStatement(sql);
                ps.setInt(1, currentUser.getId());
                rs = ps.executeQuery();
                rs.next();
                double remainingFine = rs.getDouble("fine");
                
                System.out.printf("\nPayment successful!%n");
                System.out.printf("Amount paid: $%.2f%n", paymentAmount);
                System.out.printf("Remaining balance: $%.2f%n", remainingFine);
                
                // Update currentUser's fine amount
                currentUser.setFine(remainingFine);
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error processing payment: " + e.getMessage());
        }
        promptEnter();
    }

    private void massUploadBooks() {
        clearConsole();
        System.out.println("=== Mass Upload Books ===");
        System.out.println("Please prepare a CSV file with the following format:");
        System.out.println("ISBN,Title,Author,Copies");
        System.out.println("Example: 9780061120084,To Kill a Mockingbird,Harper Lee,3");
        System.out.print("Enter the path to your CSV file: ");
        
        String filePath = scanner.nextLine();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            List<String> lines = java.nio.file.Files.readAllLines(path);
            
            int successCount = 0;
            int errorCount = 0;
            int duplicateCount = 0;
            
            // Skip header line
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                
                if (parts.length == 4) {
                    try {
                        String isbn = parts[0].trim();
                        String title = parts[1].trim();
                        String author = parts[2].trim();
                        int copies = Integer.parseInt(parts[3].trim());
                        
                        // Check if book exists
                        String checkSql = "SELECT copies FROM Book WHERE isbn = ?";
                        PreparedStatement checkPs = db.getConnection().prepareStatement(checkSql);
                        checkPs.setString(1, isbn);
                        ResultSet rs = checkPs.executeQuery();
                        
                        if (rs.next()) {
                            int existingCopies = rs.getInt("copies");
                            System.out.println("Book with ISBN " + isbn + " already exists. Adding " + copies + " copies to existing " + existingCopies + " copies.");
                            duplicateCount++;
                        }
                        
                        Book book = new Book(isbn, title, author, copies);
                        book.save();
                        successCount++;
                        checkPs.close();
                    } catch (NumberFormatException e) {
                        System.out.println("Error in line " + (i + 1) + ": Invalid number format for copies");
                        errorCount++;
                    } catch (Exception e) {
                        System.out.println("Error in line " + (i + 1) + ": " + e.getMessage());
                        errorCount++;
                    }
                } else {
                    System.out.println("Error in line " + (i + 1) + ": Invalid format");
                    errorCount++;
                }
            }
            
            System.out.println("\nUpload Summary:");
            System.out.println("Successfully added: " + successCount + " books");
            System.out.println("Duplicate books updated: " + duplicateCount + " books");
            System.out.println("Failed to add: " + errorCount + " books");
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        promptEnter();
    }

    private void massUploadUsers() {
        clearConsole();
        System.out.println("=== Mass Upload Users ===");
        System.out.println("Please prepare a CSV file with the following format:");
        System.out.println("Username,Password,FirstName,LastName,IsAdmin");
        System.out.println("Example: johndoe,password123,John,Doe,false");
        System.out.print("Enter the path to your CSV file: ");
        
        String filePath = scanner.nextLine();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            List<String> lines = java.nio.file.Files.readAllLines(path);
            
            int successCount = 0;
            int errorCount = 0;
            
            // Skip header line
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                
                if (parts.length == 5) {
                    try {
                        String username = parts[0].trim();
                        String password = parts[1].trim();
                        String fname = parts[2].trim();
                        String lname = parts[3].trim();
                        boolean isAdmin = Boolean.parseBoolean(parts[4].trim());
                        
                        PreparedStatement ps = db.getConnection().prepareStatement(
                            "INSERT INTO User (username, password, fname, lname, isAdmin) VALUES (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, username);
                        ps.setString(2, password);
                        ps.setString(3, fname);
                        ps.setString(4, lname);
                        ps.setBoolean(5, isAdmin);
                        ps.executeUpdate();
                        
                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            successCount++;
                        }
                        ps.close();
                    } catch (Exception e) {
                        System.out.println("Error in line " + (i + 1) + ": " + e.getMessage());
                        errorCount++;
                    }
                } else {
                    System.out.println("Error in line " + (i + 1) + ": Invalid format");
                    errorCount++;
                }
            }
            
            System.out.println("\nUpload Summary:");
            System.out.println("Successfully added: " + successCount + " users");
            System.out.println("Failed to add: " + errorCount + " users");
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        promptEnter();
    }

    private void updateBook() {
        clearConsole();
        System.out.println("=== Update Book ===");
        
        try {
            // First show all books
            String sql = "SELECT * FROM Book ORDER BY title";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\nAvailable Books:");
            System.out.println("ID | ISBN | Title | Author | Copies");
            System.out.println("----------------------------------------");
            
            int index = 1;
            while (rs.next()) {
                System.out.printf("%d. %s | %s | %s | %d copies%n",
                    index++,
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("copies")
                );
            }
            stmt.close();
            
            System.out.print("\nEnter the number of the book to update (or 0 to cancel): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            if (choice == 0) {
                System.out.println("Operation cancelled.");
                promptEnter();
                return;
            }
            
            // Get the selected book's ISBN
            stmt = db.getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            int currentIndex = 1;
            String selectedIsbn = null;
            
            while (rs.next() && currentIndex <= choice) {
                if (currentIndex == choice) {
                    selectedIsbn = rs.getString("isbn");
                    break;
                }
                currentIndex++;
            }
            stmt.close();
            
            if (selectedIsbn == null) {
                System.out.println("Invalid selection.");
                promptEnter();
                return;
            }
            
            Book book = Book.getBook(selectedIsbn);
            if (book == null) {
                System.out.println("Book not found.");
                promptEnter();
                return;
            }
            
            System.out.println("\nCurrent book details:");
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author: " + book.getAuthor());
            System.out.println("Copies: " + book.getCopies());
            
            System.out.print("\nEnter new title (or press Enter to keep current): ");
            String newTitle = scanner.nextLine();
            if (!newTitle.isEmpty()) {
                book.setTitle(newTitle);
            }
            
            System.out.print("Enter new author (or press Enter to keep current): ");
            String newAuthor = scanner.nextLine();
            if (!newAuthor.isEmpty()) {
                book.setAuthor(newAuthor);
            }
            
            System.out.print("Enter new number of copies (or press Enter to keep current): ");
            String copiesInput = scanner.nextLine();
            if (!copiesInput.isEmpty()) {
                try {
                    int newCopies = Integer.parseInt(copiesInput);
                    book.setCopies(newCopies);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format. Copies not updated.");
                }
            }
            
            System.out.println("Book updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
        promptEnter();
    }

    private void updateUser() {
        clearConsole();
        System.out.println("=== Update User ===");
        
        try {
            // First show all users
            String sql = "SELECT * FROM User WHERE isAdmin = false ORDER BY fname, lname";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\nAvailable Users:");
            System.out.println("ID | Name | Username");
            System.out.println("----------------------------------------");
            
            int index = 1;
            while (rs.next()) {
                System.out.printf("%d. %s %s | %s%n",
                    index++,
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("username")
                );
            }
            stmt.close();
            
            System.out.print("\nEnter the number of the user to update (or 0 to cancel): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            if (choice == 0) {
                System.out.println("Operation cancelled.");
                promptEnter();
                return;
            }
            
            // Get the selected user's ID
            stmt = db.getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            int currentIndex = 1;
            int selectedId = -1;
            String selectedUsername = null;
            
            while (rs.next() && currentIndex <= choice) {
                if (currentIndex == choice) {
                    selectedId = rs.getInt("id");
                    selectedUsername = rs.getString("username");
                    break;
                }
                currentIndex++;
            }
            stmt.close();
            
            if (selectedId == -1) {
                System.out.println("Invalid selection.");
                promptEnter();
                return;
            }
            
            // Get current user details
            String userSql = "SELECT * FROM User WHERE id = ?";
            PreparedStatement userPs = db.getConnection().prepareStatement(userSql);
            userPs.setInt(1, selectedId);
            ResultSet userRs = userPs.executeQuery();
            
            if (userRs.next()) {
                System.out.println("\nCurrent user details:");
                System.out.println("Username: " + userRs.getString("username"));
                System.out.println("First Name: " + userRs.getString("fname"));
                System.out.println("Last Name: " + userRs.getString("lname"));
                
                System.out.print("\nEnter new username (or press Enter to keep current): ");
                String newUsername = scanner.nextLine();
                if (newUsername.isEmpty()) {
                    newUsername = userRs.getString("username");
                }
                
                System.out.print("Enter new password (or press Enter to keep current): ");
                String newPassword = scanner.nextLine();
                if (newPassword.isEmpty()) {
                    newPassword = userRs.getString("password");
                }
                
                System.out.print("Enter new first name (or press Enter to keep current): ");
                String newFname = scanner.nextLine();
                if (newFname.isEmpty()) {
                    newFname = userRs.getString("fname");
                }
                
                System.out.print("Enter new last name (or press Enter to keep current): ");
                String newLname = scanner.nextLine();
                if (newLname.isEmpty()) {
                    newLname = userRs.getString("lname");
                }
                
                // Update user
                String updateSql = "UPDATE User SET username = ?, password = ?, fname = ?, lname = ? WHERE id = ?";
                PreparedStatement updatePs = db.getConnection().prepareStatement(updateSql);
                updatePs.setString(1, newUsername);
                updatePs.setString(2, newPassword);
                updatePs.setString(3, newFname);
                updatePs.setString(4, newLname);
                updatePs.setInt(5, selectedId);
                updatePs.executeUpdate();
                updatePs.close();
                
                System.out.println("User updated successfully!");
            }
            userPs.close();
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
        promptEnter();
    }

    private void updateMyProfile() {
        clearConsole();
        System.out.println("=== Update My Profile ===");
        
        try {
            System.out.println("\nCurrent profile details:");
            System.out.println("Username: " + currentUser.getUsername());
            System.out.println("First Name: " + currentUser.getFirstName());
            System.out.println("Last Name: " + currentUser.getLastName());
            
            System.out.print("\nEnter new username (or press Enter to keep current): ");
            String newUsername = scanner.nextLine();
            if (newUsername.isEmpty()) {
                newUsername = currentUser.getUsername();
            }
            
            System.out.print("Enter new password (or press Enter to keep current): ");
            String newPassword = scanner.nextLine();
            if (newPassword.isEmpty()) {
                newPassword = currentUser.getPassword();
            }
            
            System.out.print("Enter new first name (or press Enter to keep current): ");
            String newFname = scanner.nextLine();
            if (newFname.isEmpty()) {
                newFname = currentUser.getFirstName();
            }
            
            System.out.print("Enter new last name (or press Enter to keep current): ");
            String newLname = scanner.nextLine();
            if (newLname.isEmpty()) {
                newLname = currentUser.getLastName();
            }
            
            // Update user
            String updateSql = "UPDATE User SET username = ?, password = ?, fname = ?, lname = ? WHERE id = ?";
            PreparedStatement updatePs = db.getConnection().prepareStatement(updateSql);
            updatePs.setString(1, newUsername);
            updatePs.setString(2, newPassword);
            updatePs.setString(3, newFname);
            updatePs.setString(4, newLname);
            updatePs.setInt(5, currentUser.getId());
            updatePs.executeUpdate();
            updatePs.close();
            
            // Update current user object
            currentUser.updateUser(newUsername, newPassword, newFname, newLname);
            
            System.out.println("Profile updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
        promptEnter();
    }

    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void promptEnter(){
        System.out.println("Press \"ENTER\" to continue...");
        scanner.nextLine();
     }
}