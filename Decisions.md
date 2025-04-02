# Library Management

## Plan: 
-Book Class (with all book properites):

      Constructor: Book(int isbn, String title, String author, int copies)
    
-User Class (with all user properties utilizing arraylists):

      Constructor: User(int id, String fname, String lname)
      There will be a double fine variable and borrowedbooks arraylist within the constructor as well
-Transaction Class (handle all transactions utilizing java.Time.LocalDate) 

      Constructors: Transaction(int transactionId, User user, Book book) {
              this.transactionId = transactionId;
              this.user = user;
              this.book = book;
              this.borrowDate = LocalDate.now();
              this.fine = 0.0;
        
      Transaction(int transactionId, User user, Book book, LocalDate borrowDate) {
              this.transactionId = transactionId;
              this.user = user;
              this.book = book;
              this.borrowDate = borrowDate;
              this.fine = 0.0;
      



      
              

      
