package model;

public class Wishlist {
    private String userEmail;
    private int bookId;

    public Wishlist(String userEmail, int bookId) {
        this.userEmail = userEmail;
        this.bookId = bookId;
    }

    public String getUserEmail() { return userEmail; }
    public int getBookId() { return bookId; }
}