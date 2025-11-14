package controller.app;

import app.Session;
import dao.BookDAO;
import dao.LoanDAO;
import dao.factory.DAOFactory;
import model.Account;
import model.Book;
import model.Loan;
import utils.Constants;
import utils.LoanResult;

import java.util.List;

public class LoanController {

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
        Book book = bookDAO.getBookById(bookId);

        if (book == null) {
            return LoanResult.ERROR;
        }

        // Verifica se l'utente ha prestiti scaduti
        if (hasExpiredLoans(user.getEmail())) {
            return LoanResult.EXPIRED_LOAN_EXISTS;
        }

        // Verifica il numero massimo di prestiti attivi
        if (getActiveLoansCount(user.getEmail()) >= Constants.MAX_ACTIVE_LOANS) {
            return LoanResult.MAX_LOANS_REACHED;
        }

        // Verifica disponibilità
        if (book.getStock() <= 0) {
            return LoanResult.INSUFFICIENT_STOCK;
        }

        try {
            // Aggiorna lo stock nel database (diminuisce di 1)
            book.setStock(book.getStock() - 1);
            bookDAO.updateBook(book);
            loanDAO.addLoan(user.getEmail(), bookId);

            return LoanResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return LoanResult.ERROR;
        }
    }

    boolean hasExpiredLoans(String userEmail) {
        List<Loan> activeLoans = loanDAO.getActiveLoansByUser(userEmail);
        return activeLoans.stream().anyMatch(Loan::isExpired);
    }

    int getActiveLoansCount(String userEmail) {
        List<Loan> activeLoans = loanDAO.getActiveLoansByUser(userEmail);
        return activeLoans.size(); 
    }

    // Metodi aggiuntivi utili per la gestione dei prestiti
    public List<Loan> getUserActiveLoans() {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) {
            return List.of();
        }
        return loanDAO.getActiveLoansByUser(session.getLoggedUser().getEmail());
    }

    public List<Loan> getUserAllLoans() {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) {
            return List.of();
        }
        return loanDAO.getLoansByUser(session.getLoggedUser().getEmail());
    }

    public boolean returnBook(int loanId) {
        try {
            loanDAO.returnLoan(loanId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}