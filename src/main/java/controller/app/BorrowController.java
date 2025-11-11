package controller.app;

import app.Session;
import dao.BookDAO;
import dao.factory.DAOFactory;
import dao.factory.LoanDAO;
import model.Account;
import model.Book;
import model.Loan;
import utils.BorrowResult;

import java.time.LocalDate;
import java.util.List;

public class BorrowController {

    private final BookDAO bookDAO;
    private final LoanDAO loanDAO;
    static final int MAX_ACTIVE_LOANS = 3;

    public BorrowController() {
        DAOFactory factory = DAOFactory.getActiveFactory();
        this.bookDAO = factory.getBookDAO();
        this.loanDAO = factory.getLoanDAO();
    }

    public BorrowResult borrowBook(int bookId) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            return BorrowResult.NOT_LOGGED;
        }

        Account user = session.getLoggedUser();
        Book book = bookDAO.getBookById(bookId);

        if (book == null) {
            return BorrowResult.ERROR;
        }

        // Verifica se l'utente ha prestiti scaduti
        if (hasExpiredLoans(user.getEmail())) {
            return BorrowResult.EXPIRED_LOAN_EXISTS;
        }

        // Verifica il numero massimo di prestiti attivi
        if (getActiveLoansCount(user.getEmail()) >= MAX_ACTIVE_LOANS) {
            return BorrowResult.MAX_LOANS_REACHED;
        }

        // Verifica disponibilità
        if (book.getStock() <= 0) {
            return BorrowResult.INSUFFICIENT_STOCK;
        }

        try {
            // Aggiorna lo stock nel database (diminuisce di 1)
            book.setStock(book.getStock() - 1);
            bookDAO.updateBook(book);

            // Crea il prestito (30 giorni dalla data corrente)
            LocalDate loanDate = LocalDate.now();
            LocalDate dueDate = loanDate.plusDays(30);
            loanDAO.addLoan(user.getEmail(), bookId, loanDate, dueDate);

            return BorrowResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return BorrowResult.ERROR;
        }
    }

    boolean hasExpiredLoans(String userEmail) {
        List<Loan> activeLoans = loanDAO.getActiveLoansByUser(userEmail);
        return activeLoans.stream().anyMatch(loan -> loan.isExpired() && !loan.isReturned());
    }

    int getActiveLoansCount(String userEmail) {
        List<Loan> activeLoans = loanDAO.getActiveLoansByUser(userEmail);
        return (int) activeLoans.stream().filter(loan -> !loan.isReturned()).count();
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