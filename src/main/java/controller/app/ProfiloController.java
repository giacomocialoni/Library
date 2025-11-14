package controller.app;

import dao.BookDAO;
import dao.LoanDAO;
import dao.PurchaseDAO;
import dao.UserDAO;
import dao.factory.DAOFactory;
import model.Book;
import model.Loan;
import model.Purchase;
import model.User;

import java.util.List;

public class ProfiloController {

    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final LoanDAO loanDAO;

    public ProfiloController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
        this.loanDAO = DAOFactory.getActiveFactory().getLoanDAO();
    }

    public User getUser(String email) {
        return userDAO.getUserByEmail(email);
    }

    public List<Book> getPurchasedBooks(String email) {
        return bookDAO.getPurchasedBooks(email);
    }

    public List<Loan> getActiveLoans(String email) {
        return loanDAO.getActiveLoansByUser(email); 
    }
    
    public List<Purchase> getUserPurchases(String email) {
        PurchaseDAO purchaseDAO = DAOFactory.getActiveFactory().getPurchaseDAO();
        return purchaseDAO.getPurchasesByUser(email);
    }
}