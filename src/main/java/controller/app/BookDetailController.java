package controller.app;

import app.Session;
import dao.BookDAO;
import dao.factory.DAOFactory;
import model.Account;
import model.Book;

public class BookDetailController {

    private final BookDAO bookDAO;

    public BookDetailController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    }

    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }

    public void buyBook(int bookId, int quantity) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            System.out.println("Non sei loggato, non puoi acquistare libri.");
            return;
        }

        Account user = session.getLoggedUser();
        System.out.println("Acquisto " + quantity + " copia/e del libro ID: " + bookId +
                           " da parte di " + user.getEmail());

        // TODO: logica per ridurre stock e registrare acquisto nel database
    }

    public void borrowBook(int bookId, int quantity) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            System.out.println("Non sei loggato, non puoi prendere libri in prestito.");
            return;
        }

        Account user = session.getLoggedUser();
        System.out.println("Prestito " + quantity + " copia/e del libro ID: " + bookId +
                           " da parte di " + user.getEmail());

        // TODO: logica per registrare prestito nel database
    }
}