package controller.app;

import app.Session;
import dao.BookDAO;
import dao.PurchaseDAO;
import dao.factory.DAOFactory;
import model.Account;
import model.Book;
import utils.BuyResult;

public class PurchaseController {

    private final BookDAO bookDAO;
    private final PurchaseDAO purchaseDAO;

    public PurchaseController() {
        DAOFactory factory = DAOFactory.getActiveFactory();
        this.bookDAO = factory.getBookDAO();
        this.purchaseDAO = factory.getPurchaseDAO();
    }

    public BuyResult buyBook(int bookId, int quantity) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            return BuyResult.NOT_LOGGED;
        }

        Account user = session.getLoggedUser();
        Book book = bookDAO.getBookById(bookId);

        if (book == null) {
            return BuyResult.ERROR;
        }

        if (book.getStock() < quantity) {
            return BuyResult.INSUFFICIENT_STOCK;
        }

        try {
            // Aggiorna lo stock nel database
            book.setStock(book.getStock() - quantity);
            bookDAO.updateBook(book);

            // Registra UN SOLO record in purchase (non importa la quantità)
            purchaseDAO.addReservedPurchase(user.getEmail(), bookId);

            return BuyResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return BuyResult.ERROR;
        }
    }

    // Metodo per verificare se un utente ha già acquistato un libro
    public boolean hasPurchasedBook(int bookId) {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) {
            return false;
        }
        
        Account user = session.getLoggedUser();
        return purchaseDAO.hasUserPurchasedBook(user.getEmail(), bookId);
    }
}