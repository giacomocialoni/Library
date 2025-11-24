package controller.app;

import app.Session;
import dao.BookDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BuyResult;
import utils.LoanResult;
import utils.Constants;

public class BookDetailController {

    private static final Logger logger = LoggerFactory.getLogger(BookDetailController.class);

    private final PurchaseController purchaseController;
    private final LoanController loanController;
    private final BookDAO bookDAO;

    public BookDetailController() {
        this.purchaseController = new PurchaseController();
        this.loanController = new LoanController();
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    }

    public Book getBookById(int bookId) {
        try {
            return bookDAO.getBookById(bookId);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro non trovato con ID: " + bookId);
            return null;
        } catch (DAOException e) {
            logger.error("Errore durante il recupero del libro con ID: " + bookId, e);
            return null;
        }
    }

    public BuyResult buyBook(int bookId, int quantity) {
        return purchaseController.buyBook(bookId, quantity);
    }

    public LoanResult loanBook(int bookId) throws DAOException {
        return loanController.loanBook(bookId);
    }

    public boolean hasPurchasedBook(int bookId) {
        return purchaseController.hasPurchasedBook(bookId);
    }

    public boolean canLoanBook(int bookId) {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) {
            return false;
        }

        Book book = getBookById(bookId);
        if (book == null || book.getStock() <= 0) {
            return false;
        }

        return !loanController.hasExpiredLoans(session.getLoggedUser().getEmail()) &&
		       loanController.getActiveLoansCount(session.getLoggedUser().getEmail()) < Constants.MAX_ACTIVE_LOANS;
    }
}