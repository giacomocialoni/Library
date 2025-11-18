package controller.app;

import dao.BookDAO;
import dao.LoanDAO;
import dao.PurchaseDAO;
import dao.UserDAO;
import dao.factory.DAOFactory;
import dao.database.DatabaseUserDAO;
import model.Book;
import model.Loan;
import model.Purchase;
import model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationController {

    private final PurchaseDAO purchaseDAO;
    private final LoanDAO loanDAO;
    private final UserDAO userDAO;
    private final BookDAO bookDAO;

    public ReservationController() {
        this.purchaseDAO = DAOFactory.getActiveFactory().getPurchaseDAO();
        this.loanDAO = DAOFactory.getActiveFactory().getLoanDAO();
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    }

    // ===== RICERCHE PER UTENTE =====

    public List<User> searchUsers(String searchText) {
        if (userDAO instanceof DatabaseUserDAO) {
            return ((DatabaseUserDAO) userDAO).searchUsers(searchText);
        }

        List<User> allUsers = userDAO.getAllUsers();
        return allUsers.stream()
                .filter(user -> 
                        user.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                        user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                        user.getLastName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Purchase> searchPurchasesByUser(String searchText) {
        List<Purchase> allPurchases = getAllReservedPurchases();
        return allPurchases.stream()
                .filter(purchase -> {
                    User user = userDAO.getUserByEmail(purchase.getUserEmail());
                    return user != null &&
                           (user.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getLastName().toLowerCase().contains(searchText.toLowerCase()));
                })
                .collect(Collectors.toList());
    }

    public List<Loan> searchLoansByUser(String searchText) {
        List<Loan> allLoans = getAllReservedLoans();
        return allLoans.stream()
                .filter(loan -> {
                    User user = userDAO.getUserByEmail(loan.getUserEmail());
                    return user != null &&
                           (user.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getLastName().toLowerCase().contains(searchText.toLowerCase()));
                })
                .collect(Collectors.toList());
    }

    // ===== RICERCHE PER LIBRO =====

    public List<Purchase> searchPurchasesByBook(String searchText) {
        List<Purchase> allPurchases = getAllReservedPurchases();
        return allPurchases.stream()
                .filter(purchase -> {
                    Book book = bookDAO.getBookById(purchase.getBookId());
                    return book != null &&
                           (book.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                            book.getAuthor().toLowerCase().contains(searchText.toLowerCase()));
                })
                .collect(Collectors.toList());
    }

    public List<Loan> searchLoansByBook(String searchText) {
        List<Loan> allLoans = getAllReservedLoans();
        return allLoans.stream()
                .filter(loan -> {
                    Book book = bookDAO.getBookById(loan.getBookId());
                    return book != null &&
                           (book.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                            book.getAuthor().toLowerCase().contains(searchText.toLowerCase()));
                })
                .collect(Collectors.toList());
    }

    // ===== RICERCHE GENERICHE (per compatibilità) =====

    public List<Purchase> searchPurchases(String searchText) {
        List<Purchase> allPurchases = getAllReservedPurchases();
        return allPurchases.stream()
                .filter(purchase -> {
                    Book book = bookDAO.getBookById(purchase.getBookId());
                    User user = userDAO.getUserByEmail(purchase.getUserEmail());
                    return book != null && user != null &&
                           (book.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                            book.getAuthor().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getLastName().toLowerCase().contains(searchText.toLowerCase()));
                })
                .collect(Collectors.toList());
    }

    public List<Loan> searchLoans(String searchText) {
        List<Loan> allLoans = getAllReservedLoans();
        return allLoans.stream()
                .filter(loan -> {
                    Book book = bookDAO.getBookById(loan.getBookId());
                    User user = userDAO.getUserByEmail(loan.getUserEmail());
                    return book != null && user != null &&
                           (book.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                            book.getAuthor().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getLastName().toLowerCase().contains(searchText.toLowerCase()));
                })
                .collect(Collectors.toList());
    }

    // ===== METODI DI ACCESSO AI DATI =====

    public List<Purchase> getReservedPurchasesByUser(String email) {
        return purchaseDAO.getPurchasesByUser(email)
                .stream()
                .filter(Purchase::isReserved)
                .collect(Collectors.toList());
    }

    public List<Purchase> getAllReservedPurchases() {
        return purchaseDAO.getAllPurchases()
                .stream()
                .filter(Purchase::isReserved)
                .collect(Collectors.toList());
    }

    public List<Loan> getReservedLoansByUser(String email) {
        return loanDAO.getReservedLoansByUser(email);
    }

    public List<Loan> getAllReservedLoans() {
        return loanDAO.getAllReservedLoans();
    }

    // ===== METODI DI GESTIONE =====

    public void acceptPurchase(int purchaseId) {
        purchaseDAO.acceptPurchase(purchaseId);
    }

    public void acceptLoan(int loanId) {
        loanDAO.acceptedLoan(loanId);
    }

    public void updateBookStock(int bookId, int quantityChange) {
        Book book = bookDAO.getBookById(bookId);
        if (book == null) return;

        int newStock = Math.max(0, book.getStock() + quantityChange);
        book.setStock(newStock);
        bookDAO.updateBook(book);
    }

    // ===== METODI AGGIUNTIVI UTILI =====

    /**
     * Ottiene tutte le prenotazioni (vendite + prestiti) per un utente specifico
     */
    public List<Object> getAllReservationsForUser(String userEmail) {
        List<Purchase> purchases = getReservedPurchasesByUser(userEmail);
        List<Loan> loans = getReservedLoansByUser(userEmail);
        
        return List.of(purchases, loans);
    }

    /**
     * Conta il numero totale di prenotazioni in sospeso
     */
    public int getTotalPendingReservations() {
        int salesCount = getAllReservedPurchases().size();
        int loansCount = getAllReservedLoans().size();
        return salesCount + loansCount;
    }
}