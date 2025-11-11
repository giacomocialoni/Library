package controller.app;

import app.Session;
import dao.factory.DAOFactory;
import model.Book;
import utils.BorrowResult;
import utils.BuyResult;

public class BookDetailController {

    private final PurchaseController purchaseController;
    private final BorrowController borrowController;

    public BookDetailController() {
        this.purchaseController = new PurchaseController();
        this.borrowController = new BorrowController();
    }

    public Book getBookById(int bookId) {
        return DAOFactory.getActiveFactory().getBookDAO().getBookById(bookId);
    }

    public BuyResult buyBook(int bookId, int quantity) {
        return purchaseController.buyBook(bookId, quantity);
    }

    public BorrowResult borrowBook(int bookId) {
        return borrowController.borrowBook(bookId);
    }

    // Metodi delegati aggiuntivi
    public boolean hasPurchasedBook(int bookId) {
        return purchaseController.hasPurchasedBook(bookId);
    }

    public boolean canBorrowBook(int bookId) {
        // Verifica se l'utente può prendere in prestito il libro
        // senza effettivamente eseguire il prestito
        Session session = app.Session.getInstance();
        if (!session.isLoggedIn()) {
            return false;
        }
        
        Book book = getBookById(bookId);
        if (book == null || book.getStock() <= 0) {
            return false;
        }

        return !borrowController.hasExpiredLoans(session.getLoggedUser().getEmail()) &&
               borrowController.getActiveLoansCount(session.getLoggedUser().getEmail()) < BorrowController.MAX_ACTIVE_LOANS;
    }
}