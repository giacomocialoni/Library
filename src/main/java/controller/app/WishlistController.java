package controller.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.BookDAO;
import dao.UserDAO;
import dao.WishlistDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.Book;
import model.User;
import model.Wishlist;

public class WishlistController {
	private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);
    private final UserDAO userDAO;
    private final WishlistDAO wishlistDAO;
    private final BookDAO bookDAO;
    
	public WishlistController() {
		this.wishlistDAO = DAOFactory.getActiveFactory().getWishlistDAO();
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
	}

    public User getUser(String email) {
        try {
            return userDAO.getUserByEmail(email);
        } catch (RecordNotFoundException e) {
            logger.warn("Utente non trovato: " + email);
            return null;
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero utente: " + email, e);
            return null;
        }
    }

    public List<Book> getWishlistBooks(String email) {
        try {
            List<Wishlist> wishlist = wishlistDAO.getWishlistByUser(email);
            List<Book> books = new ArrayList<>();

            for (Wishlist w : wishlist) {
                Book b = bookDAO.getBookById(w.getBookId());
                if (b != null) books.add(b);
            }

            return books;

        } catch (Exception e) {
            logger.error("Errore durante il recupero dei libri in wishlist", e);
            return List.of();
        }
    }
    
    public void addToWishlist(String email, int bookId) throws DAOException {
        wishlistDAO.addToWishlist(email, bookId);
    }

    public void removeFromWishlist(String email, int bookId) throws DAOException {
        wishlistDAO.removeFromWishlist(email, bookId);
    }
}
