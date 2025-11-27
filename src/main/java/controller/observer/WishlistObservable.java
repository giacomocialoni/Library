package controller.observer;

import model.Book;

import java.util.ArrayList;
import java.util.List;

public class WishlistObservable {

    private final List<WishlistObserver> observers = new ArrayList<>();

    public void addObserver(WishlistObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(WishlistObserver observer) {
        observers.remove(observer);
    }

    public void notifyBookAvailable(Book book) {
        for (WishlistObserver observer : observers) {
            observer.onBookAvailable(book);
        }
    }
}