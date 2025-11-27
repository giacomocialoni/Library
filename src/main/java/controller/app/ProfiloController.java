package controller.app;

import dao.BookDAO;
import dao.LoanDAO;
import dao.PurchaseDAO;
import dao.UserDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.Book;
import model.Loan;
import model.Purchase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProfiloController {

    private static final Logger logger = LoggerFactory.getLogger(ProfiloController.class);

    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final LoanDAO loanDAO;
    private final PurchaseDAO purchaseDAO;

    public ProfiloController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
        this.loanDAO = DAOFactory.getActiveFactory().getLoanDAO();
        this.purchaseDAO = DAOFactory.getActiveFactory().getPurchaseDAO();
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

    public List<Book> getPurchasedBooks(String email) {
        try {
            return bookDAO.getPurchasedBooks(email);
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero libri acquistati per utente: " + email, e);
            return List.of();
        }
    }

    public List<Loan> getActiveLoans(String email) {
        try {
            return loanDAO.getActiveLoansByUser(email);
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero prestiti attivi per utente: " + email, e);
            return List.of();
        }
    }

    public List<Purchase> getUserPurchases(String email) {
        try {
            return purchaseDAO.getPurchasesByUser(email);
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero acquisti per utente: " + email, e);
            return List.of();
        }
    }
}