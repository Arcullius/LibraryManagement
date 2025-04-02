public class Book{
    
    //book variables
    private int isbn;
    private String title, author;
    private boolean isAvailable;
    private int copies;

    //constructor
    public Book(int isbn, String title, String author, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.isAvailable = copies > 0;
    }

    //get and set methods 
    public int getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public int getCopies() { return copies; }
    
    public void setIsbn(int isbn){ this.isbn = isbn; }
    public void setIsAvailable(boolean available) { isAvailable = available; }
    public void setCopies(int copies) { 
        this.copies = copies;
        this.isAvailable = copies > 0;
    }

    //toString method
    @Override
    public String toString() {
        return "Book: {" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isAvailable=" + isAvailable +
                ", copies=" + copies +
                '}';
    }
}
