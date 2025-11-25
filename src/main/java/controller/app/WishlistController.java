package controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.UserDAO;
import dao.WishlistDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.User;

public class WishlistController {

    private static final Logger logger = LoggerFactory.getLogger(ProfiloController.class);
    private final UserDAO userDAO;
    private final WishlistDAO wishlistDAO;
    
	public WishlistController() {
		this.wishlistDAO = DAOFactory.getActiveFactory().getWishlistDAO();
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
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
}
