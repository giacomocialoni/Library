package controller.app;

import app.Session;
import dao.BookDAO;
import dao.LoanDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.Account;
import model.Book;
import model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.LoanResult;

import java.util.List;

public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    private final BookDAO bookDAO;
    private final LoanDAO loanDAO;

    public LoanController() {
        DAOFactory factory = DAOFactory.getActiveFactory();
        this.bookDAO = factory.getBookDAO();
        this.loanDAO = factory.getLoanDAO();
    }

    public LoanResult loanBook(int bookId) {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) {
            return LoanResult.NOT_LOGGED;
        }

        Account user = session.getLoggedUser();
        Book book;
        try {
            book = bookDAO.getBookById(bookId);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro non trovato con id=" + bookId);
            return LoanResult.ERROR;
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero del libro id=" + bookId, e);
            return LoanResult.ERROR;
        }

        if (hasExpiredLoans(user.getEmail())) {
            return LoanResult.EXPIRED_LOAN_EXISTS;
        }

        if (getActiveLoansCount(user.getEmail()) >= Constants.MAX_ACTIVE_LOANS) {
            return LoanResult.MAX_LOANS_REACHED;
        }

        if (book.getStock() <= 0) {
            return LoanResult.INSUFFICIENT_STOCK;
        }

        try {
            book.setStock(book.getStock() - 1);
            bookDAO.updateBook(book);
            loanDAO.addLoan(user.getEmail(), bookId);
            return LoanResult.SUCCESS;
        } catch (RecordNotFoundException e) {
            logger.warn("Errore: libro da aggiornare non trovato id=" + bookId, e);
            return LoanResult.ERROR;
        } catch (DAOException e) {
            logger.error("Errore DAO durante il prestito libro id=" + bookId + " per utente=" + user.getEmail(), e);
            return LoanResult.ERROR;
        }
    }

    public boolean returnBook(int loanId) {
        try {
            loanDAO.returnLoan(loanId);
            return true;
        } catch (RecordNotFoundException e) {
            logger.warn("Prestito non trovato con id=" + loanId, e);
            return false;
        } catch (DAOException e) {
            logger.error("Errore DAO durante il reso del prestito id=" + loanId, e);
            return false;
        }
    }

    boolean hasExpiredLoans(String userEmail) {
        try {
            List<Loan> activeLoans = loanDAO.getActiveLoansByUser(userEmail);
            return activeLoans.stream().anyMatch(Loan::isExpired);
        } catch (RecordNotFoundException e) {
            // Nessun prestito attivo => nessun prestito scaduto
            return false;
        } catch (DAOException e) {
            logger.error("Errore DAO durante il controllo prestiti scaduti per user=" + userEmail, e);
            return false;
        }
    }

    int getActiveLoansCount(String userEmail) {
        try {
            return loanDAO.getActiveLoansByUser(userEmail).size();
        } catch (RecordNotFoundException e) {
            // Nessun prestito attivo
            return 0;
        } catch (DAOException e) {
            logger.error("Errore DAO durante il conteggio prestiti attivi per user=" + userEmail, e);
            return 0;
        }
    }

    public List<Loan> getUserActiveLoans() {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) return List.of();

        try {
            return loanDAO.getActiveLoansByUser(session.getLoggedUser().getEmail());
        } catch (RecordNotFoundException e) {
            return List.of();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero prestiti attivi per user=" + session.getLoggedUser().getEmail(), e);
            return List.of();
        }
    }

    public List<Loan> getUserAllLoans() {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) return List.of();

        try {
            return loanDAO.getLoansByUser(session.getLoggedUser().getEmail());
        } catch (RecordNotFoundException e) {
            return List.of();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero di tutti i prestiti per user=" + session.getLoggedUser().getEmail(), e);
            return List.of();
        }
    }
}