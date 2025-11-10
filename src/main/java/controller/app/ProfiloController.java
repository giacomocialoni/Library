package controller.app;

import dao.BookDAO;
import dao.UserDAO;
import dao.factory.DAOFactory;
import model.Book;
import model.Loan;
import model.User;

import java.util.List;

public class ProfiloController {

    private final BookDAO bookDAO;
    private final UserDAO userDAO;

    public ProfiloController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
    }

    public User getUser(String email) {
        return userDAO.getUserByEmail(email);
    }

    public List<Book> getPurchasedBooks(String email) {
        return bookDAO.getPurchasedBooks(email);
    }

    public List<Loan> getLoans(String email) {
        return bookDAO.getLoanedBooks(email); 
    }
}