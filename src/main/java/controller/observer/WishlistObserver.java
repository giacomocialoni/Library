package controller.observer;

import model.Book;

public interface WishlistObserver {
    void onBookAvailable(Book book);
}