package controller.app;

import app.Session;
import bean.BookBean;
import dao.BookDAO;
import dao.WishlistDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BuyResult;
import utils.LoanResult;
import utils.Constants;

public class BookDetailController {

    private static final Logger logger = LoggerFactory.getLogger(BookDetailController.class);

    private final PurchaseController purchaseController;
    private final LoanController loanController;
    private final BookDAO bookDAO;
    private final WishlistDAO wishlistDAO;

    public BookDetailController() {
        this.purchaseController = new PurchaseController();
        this.loanController = new LoanController();
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
        this.wishlistDAO = DAOFactory.getActiveFactory().getWishlistDAO();
    }

    // ================== READ ==================
    public BookBean getBookById(int bookId) {
        try {
            Book book = bookDAO.getBookById(bookId);
            return toBean(book);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro non trovato con ID: {}", bookId);
            return null;
        } catch (DAOException e) {
            logger.error("Errore durante il recupero del libro con ID: {}", bookId, e);
            return null;
        }
    }

    // ================== BUY / LOAN ==================
    public BuyResult buyBook(int bookId, int quantity) {
        return purchaseController.buyBook(bookId, quantity);
    }

    public LoanResult loanBook(int bookId) {
        return loanController.loanBook(bookId);
    }

    public boolean hasPurchasedBook(int bookId) {
        return purchaseController.hasPurchasedBook(bookId);
    }

    public boolean canLoanBook(int bookId) {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) return false;

        Book book = getBookEntityById(bookId);
        if (book == null || book.getStock() <= 0) return false;

        String email = session.getLoggedUser().getEmail();
        return !loanController.hasExpiredLoans(email) &&
               loanController.getActiveLoansCount(email) < Constants.MAX_ACTIVE_LOANS;
    }

    // ================== WISHLIST ==================
    public boolean isInWishlist(int bookId) {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) return false;

        try {
            return wishlistDAO.isInWishlist(session.getLoggedUser().getEmail(), bookId);
        } catch (DAOException e) {
            logger.error("Errore nel controllo wishlist", e);
            return false;
        }
    }

    public boolean addToWishlist(int bookId) {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) return false;

        try {
            wishlistDAO.addToWishlist(session.getLoggedUser().getEmail(), bookId);
            return true;
        } catch (DAOException e) {
            logger.error("Errore nell'aggiunta alla wishlist", e);
            return false;
        }
    }

    public boolean removeFromWishlist(int bookId) {
        Session session = Session.getInstance();
        if (!session.isLoggedIn()) return false;

        try {
            wishlistDAO.removeFromWishlist(session.getLoggedUser().getEmail(), bookId);
            return true;
        } catch (DAOException e) {
            logger.error("Errore nella rimozione dalla wishlist", e);
            return false;
        }
    }

    // ================== PRIVATE HELPERS ==================
    private BookBean toBean(Book book) {
        BookBean bean = new BookBean();
        try {
            bean.setId(book.getId());
            bean.setTitle(book.getTitle());
            bean.setAuthor(book.getAuthor());
            bean.setCategory(book.getCategory());
            bean.setImagePath(book.getImagePath());
            bean.setStock(book.getStock());
            bean.setPrice(book.getPrice());
            bean.setPublisher(book.getPublisher());
            bean.setYear(book.getYear());
            bean.setPages(book.getPages());
            bean.setIsbn(book.getIsbn());
            bean.setPlot(book.getPlot());
        } catch (Exception e) {
            logger.warn("Errore nella creazione del BookBean per libro id={}", book.getId(), e);
        }
        return bean;
    }

    private Book getBookEntityById(int bookId) {
        try {
            return bookDAO.getBookById(bookId);
        } catch (Exception e) {
            logger.warn("Errore nel recupero entity libro id={}", bookId, e);
            return null;
        }
    }
}