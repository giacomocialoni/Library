package controller.app;

import app.Session;
import dao.BookDAO;
import dao.factory.DAOFactory;
import model.Account;
import model.Book;
import utils.BorrowResult;
import utils.BuyResult;

public class BookDetailController {

    private final BookDAO bookDAO;

    public BookDetailController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    }

    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }

    public BuyResult buyBook(int bookId, int quantity) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            return BuyResult.NOT_LOGGED;
        }
        Account user = session.getLoggedUser();
        try {
            // TODO: logica vera per lo stock e l'acquisto
            System.out.println("Acquisto " + quantity + " copia/e del libro ID: " + bookId +
                               " da parte di " + user.getEmail());
            return BuyResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return BuyResult.ERROR;
        }
    }

    public BorrowResult borrowBook(int bookId, int quantity) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            return BorrowResult.NOT_LOGGED;
        }
        Account user = session.getLoggedUser();
        try {
            // TODO: logica vera per lo stock e l'acquisto
            System.out.println("Prestito " + quantity + " copia/e del libro ID: " + bookId +
                               " da parte di " + user.getEmail());
            return BorrowResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return BorrowResult.ERROR;
        }
    }
}