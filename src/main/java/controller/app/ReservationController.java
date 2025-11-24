package controller.app;

import dao.BookDAO;
import dao.LoanDAO;
import dao.PurchaseDAO;
import dao.UserDAO;
import dao.database.DatabaseUserDAO;
import dao.factory.DAOFactory;
import model.Book;
import model.Loan;
import model.Purchase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

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
        try {
            if (userDAO instanceof DatabaseUserDAO) {
                return ((DatabaseUserDAO) userDAO).searchUsers(searchText);
            }
            String lowerSearch = searchText.toLowerCase();
            return userDAO.getAllUsers().stream()
                    .filter(u -> u.getEmail().toLowerCase().contains(lowerSearch) ||
                                 u.getFirstName().toLowerCase().contains(lowerSearch) ||
                                 u.getLastName().toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Errore durante la ricerca utenti con testo '{}'", searchText, e);
            return List.of();
        }
    }

    public List<Purchase> searchPurchasesByUser(String searchText) {
        String lowerSearch = searchText.toLowerCase();
        return getAllReservedPurchases().stream()
                .filter(p -> {
                    try {
                        User user = userDAO.getUserByEmail(p.getUserEmail());
                        return user != null &&
                               (user.getEmail().toLowerCase().contains(lowerSearch) ||
                                user.getFirstName().toLowerCase().contains(lowerSearch) ||
                                user.getLastName().toLowerCase().contains(lowerSearch));
                    } catch (Exception e) {
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    public List<Loan> searchLoansByUser(String searchText) {
        String lowerSearch = searchText.toLowerCase();
        return getAllReservedLoans().stream()
                .filter(l -> {
                    try {
                        User user = userDAO.getUserByEmail(l.getUserEmail());
                        return user != null &&
                               (user.getEmail().toLowerCase().contains(lowerSearch) ||
                                user.getFirstName().toLowerCase().contains(lowerSearch) ||
                                user.getLastName().toLowerCase().contains(lowerSearch));
                    } catch (Exception e) {
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    // ===== RICERCHE PER LIBRO =====

    public List<Purchase> searchPurchasesByBook(String searchText) {
        String lowerSearch = searchText.toLowerCase();
        return getAllReservedPurchases().stream()
                .filter(p -> {
                    try {
                        Book book = bookDAO.getBookById(p.getBookId());
                        return book != null &&
                               (book.getTitle().toLowerCase().contains(lowerSearch) ||
                                book.getAuthor().toLowerCase().contains(lowerSearch));
                    } catch (Exception e) {
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    public List<Loan> searchLoansByBook(String searchText) {
        String lowerSearch = searchText.toLowerCase();
        return getAllReservedLoans().stream()
                .filter(l -> {
                    try {
                        Book book = bookDAO.getBookById(l.getBookId());
                        return book != null &&
                               (book.getTitle().toLowerCase().contains(lowerSearch) ||
                                book.getAuthor().toLowerCase().contains(lowerSearch));
                    } catch (Exception e) {
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    // ===== RICERCHE GENERICHE =====

    public List<Purchase> searchPurchases(String searchText) {
        String lowerSearch = searchText.toLowerCase();
        return getAllReservedPurchases().stream()
                .filter(p -> {
                    try {
                        Book book = bookDAO.getBookById(p.getBookId());
                        User user = userDAO.getUserByEmail(p.getUserEmail());
                        return book != null && user != null &&
                               (book.getTitle().toLowerCase().contains(lowerSearch) ||
                                book.getAuthor().toLowerCase().contains(lowerSearch) ||
                                user.getEmail().toLowerCase().contains(lowerSearch) ||
                                user.getFirstName().toLowerCase().contains(lowerSearch) ||
                                user.getLastName().toLowerCase().contains(lowerSearch));
                    } catch (Exception e) {
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    public List<Loan> searchLoans(String searchText) {
        String lowerSearch = searchText.toLowerCase();
        return getAllReservedLoans().stream()
                .filter(l -> {
                    try {
                        Book book = bookDAO.getBookById(l.getBookId());
                        User user = userDAO.getUserByEmail(l.getUserEmail());
                        return book != null && user != null &&
                               (book.getTitle().toLowerCase().contains(lowerSearch) ||
                                book.getAuthor().toLowerCase().contains(lowerSearch) ||
                                user.getEmail().toLowerCase().contains(lowerSearch) ||
                                user.getFirstName().toLowerCase().contains(lowerSearch) ||
                                user.getLastName().toLowerCase().contains(lowerSearch));
                    } catch (Exception e) {
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    // ===== METODI DI ACCESSO AI DATI =====

    public List<Purchase> getReservedPurchasesByUser(String email) {
        try {
            return purchaseDAO.getPurchasesByUser(email).stream()
                    .filter(Purchase::isReserved)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Errore nel recupero acquisti riservati per utente '{}'", email, e);
            return List.of();
        }
    }

    public List<Purchase> getAllReservedPurchases() {
        try {
            return purchaseDAO.getAllPurchases().stream()
                    .filter(Purchase::isReserved)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Errore nel recupero di tutti gli acquisti riservati", e);
            return List.of();
        }
    }

    public List<Loan> getReservedLoansByUser(String email) {
        try {
            return loanDAO.getReservedLoansByUser(email);
        } catch (Exception e) {
            logger.error("Errore nel recupero prestiti riservati per utente '{}'", email, e);
            return List.of();
        }
    }

    public List<Loan> getAllReservedLoans() {
        try {
            return loanDAO.getAllReservedLoans();
        } catch (Exception e) {
            logger.error("Errore nel recupero di tutti i prestiti riservati", e);
            return List.of();
        }
    }

    // ===== METODI DI GESTIONE =====

    public void acceptPurchase(int purchaseId) {
        try {
            purchaseDAO.acceptPurchase(purchaseId);
        } catch (Exception e) {
            logger.error("Errore nell'accettare acquisto '{}'", purchaseId, e);
        }
    }

    public void acceptLoan(int loanId) {
        try {
            loanDAO.acceptedLoan(loanId);
        } catch (Exception e) {
            logger.error("Errore nell'accettare prestito '{}'", loanId, e);
        }
    }

    public void updateBookStock(int bookId, int quantityChange) {
        try {
            Book book = bookDAO.getBookById(bookId);
            if (book == null) return;

            int newStock = Math.max(0, book.getStock() + quantityChange);
            book.setStock(newStock);
            bookDAO.updateBook(book);
        } catch (Exception e) {
            logger.error("Errore nell'aggiornare lo stock del libro '{}'", bookId, e);
        }
    }

    // ===== METODI AGGIUNTIVI =====

    public List<Object> getAllReservationsForUser(String userEmail) {
        return List.of(getReservedPurchasesByUser(userEmail), getReservedLoansByUser(userEmail));
    }

    public int getTotalPendingReservations() {
        return getAllReservedPurchases().size() + getAllReservedLoans().size();
    }
}