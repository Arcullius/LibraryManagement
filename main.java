public class main{
    public static void main(String[] args) {

        //Book tests
        Book b = new Book(12345678, "The Odyssey", "Homer", 3);
        
        b.setCopies(5);
        b.setIsbn(7654321);
        b.setIsAvailable(false);

        System.out.println(b.getAuthor());
        System.out.println(b.getIsbn());
        System.out.println(b.getCopies());
        System.out.println(b.getTitle());
        System.out.println(b);

        User u = new User(12345, "Billy","Bob");
        u.borrowBook(b);
        System.out.println(u.getBorrowedBooksCount());
        System.out.println(u.getBooksBorrowed());
        System.out.println(u.getFine());
        System.out.println(u.getFirstName());
        System.out.println(u.getLastName());
        System.out.println(u.getId());
        System.out.println(u.getName());
        System.out.println(u);
    }
}
