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
    private final WishlistDAO wishlistDAO;
    private final EmailService emailService;

    // Costruttore usato nell'app
    public WishlistEmailObserver() {
        this.wishlistDAO = DAOFactory.getActiveFactory().getWishlistDAO();
        this.emailService = new EmailService();
    }

    // Costruttore usato nei test
    public WishlistEmailObserver(WishlistDAO wishlistDAO, EmailService emailService) {
        this.wishlistDAO = wishlistDAO;
        this.emailService = emailService;
    }

    @Override
    public void onBookAvailable(Book book) {
        try {
            // prendi tutti gli utenti che hanno quel libro in wishlist
            List<String> users = wishlistDAO.getUsersWithBookInWishlist(book.getId());

            for (String email : users) {
                emailService.send(
                        email,
                        "Buone notizie da Bibliotech!",
                        "Ciao " + email + ",\n"
                        		+ "\nVolevamo avvisarti che il libro " + book.getTitle() + " - " + book.getAuthor()
                        		+ " è tornato disponibile nel nostro store."
                        		+ "\nPrenotalo o passa a trovarci prima che finisca di nuovo!\n"
                        		+ "\nEmail automatica inviata da Bibliotech"
                );
            }
        } catch (DAOException e) {
            logger.error("Errore durante il recupero utenti in wishlist per il libro ID " + book.getId(), e);
        }
    }
}