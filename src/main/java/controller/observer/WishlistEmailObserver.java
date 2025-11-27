package controller.observer;

import dao.WishlistDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import model.Book;
import service.EmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WishlistEmailObserver implements WishlistObserver {

    private static final Logger logger = LoggerFactory.getLogger(WishlistEmailObserver.class);

    private final WishlistDAO wishlistDAO = DAOFactory.getActiveFactory().getWishlistDAO();
    private final EmailService emailService = new EmailService();

    @Override
    public void onBookAvailable(Book book) {
        try {
            // prendi tutti gli utenti che hanno quel libro in wishlist
            List<String> users = wishlistDAO.getUsersWithBookInWishlist(book.getId());

            for (String email : users) {
                emailService.send(
                        email,
                        "Buone notizie da Bibliotech!",
                        "Ciao " + email + ",\nVolevamo avvisarti che il libro \"" + book.getTitle() + "\" è tornato disponibile nel nostro store.\nPrenotalo o passa a trovarci prima che finisca di nuovo!"
                );
            }
        } catch (DAOException e) {
            logger.error("Errore durante il recupero utenti in wishlist per il libro ID " + book.getId(), e);
        }
    }
}