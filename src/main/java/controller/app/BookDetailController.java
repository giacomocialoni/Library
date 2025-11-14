package controller.app;

import app.Session;
import dao.factory.DAOFactory;
import model.Book;
import utils.LoanResult;
import utils.BuyResult;
import utils.Constants;

public class BookDetailController {

    private final PurchaseController purchaseController;
    private final LoanController loanController;

    public BookDetailController() {
        this.purchaseController = new PurchaseController();
        this.loanController = new LoanController();
    }

    public Book getBookById(int bookId) {
        return DAOFactory.getActiveFactory().getBookDAO().getBookById(bookId);
    }

    public BuyResult buyBook(int bookId, int quantity) {
        return purchaseController.buyBook(bookId, quantity);
    }

    public LoanResult loanBook(int bookId) {
        return loanController.loanBook(bookId);
    }

    // Metodi delegati aggiuntivi
    public boolean hasPurchasedBook(int bookId) {
        return purchaseController.hasPurchasedBook(bookId);
    }

    public boolean canLoanBook(int bookId) {
        Session session = app.Session.getInstance();
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