package dao;

import model.Wishlist;
import exception.DAOException;
import exception.RecordNotFoundException;

import java.util.List;

public interface WishlistDAO {

    void addToWishlist(String userEmail, int bookId) throws DAOException;
    void removeFromWishlist(String userEmail, int bookId) throws DAOException;
    boolean isInWishlist(String userEmail, int bookId) throws DAOException;
    List<Wishlist> getWishlistByUser(String userEmail) throws DAOException, RecordNotFoundException;
    List<String> getUsersWithBookInWishlist(int bookId) throws DAOException;
}