package controller.app;

import bean.BookBean;
import bean.LoanBean;
import bean.UserBean;
import dao.BookDAO;
import dao.LoanDAO;
import dao.UserDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.IncorrectDataException;
import model.Book;
import model.Loan;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProfiloController {

    private static final Logger logger =
            LoggerFactory.getLogger(ProfiloController.class);

    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final LoanDAO loanDAO;

    public ProfiloController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
        this.loanDAO = DAOFactory.getActiveFactory().getLoanDAO();
    }

    public UserBean getUser(String email) {
        try {
            User user = userDAO.getUserByEmail(email);

            UserBean bean = new UserBean();
            bean.setEmail(user.getEmail());
            bean.setPassword(user.getPassword());
            bean.setFirstName(user.getFirstName());
            bean.setLastName(user.getLastName());
            return bean;

        } catch (DAOException e) {
            logger.error("Errore recupero utente", e);
            return null;
        }
    }

    public List<BookBean> getPurchasedBooks(String email) {
        try {
            return bookDAO.getPurchasedBooks(email)
                    .stream()
                    .map(this::toBookBean)
                    .toList();
        } catch (DAOException e) {
            logger.error("Errore libri acquistati", e);
            return List.of();
        }
    }

    public List<LoanBean> getActiveLoans(String email) {
        try {
            return loanDAO.getActiveLoansByUser(email)
                    .stream()
                    .map(this::toLoanBean)
                    .filter(loanBean -> loanBean != null && loanBean.getBook() != null) // Filtra loanBean null o senza libro
                    .toList();
        } catch (DAOException e) {
            logger.error("Errore prestiti", e);
            return List.of();
        }
    }

    /* =====================
       MAPPING PRIVATO
       ===================== */

    private BookBean toBookBean(Book book) {
        try {
            BookBean bean = new BookBean();
            bean.setId(book.getId());
            bean.setTitle(book.getTitle());
            bean.setAuthor(book.getAuthor());
            bean.setCategory(book.getCategory());
            bean.setImagePath(book.getImagePath());
            bean.setStock(book.getStock());
            return bean;
        } catch (IncorrectDataException e) {
            throw new RuntimeException("Dati libro non validi", e);
        }
    }

    private LoanBean toLoanBean(Loan loan) {
        try {
            LoanBean bean = new LoanBean();
            bean.setId(loan.getId());
            bean.setUserEmail(loan.getUserEmail());
            bean.setStatus(loan.getStatus());
            bean.setReservedDate(loan.getReservedDate());
            bean.setLoanedDate(loan.getLoanedDate());
            bean.setReturningDate(loan.getReturningDate());

            int bookId = loan.getBookId();
            
            // Controlla se bookId è valido (maggiore di 0)
            if (bookId <= 0) {
                logger.warn("BookId non valido ({}) per prestito id={}", bookId, loan.getId());
                return null; // Ritorna null, verrà filtrato dopo
            }
            
            try {
                Book book = bookDAO.getBookById(bookId);
                if (book != null) {
                    BookBean bookBean = toBookBean(book);
                    bean.setBook(bookBean);
                } else {
                    logger.warn("Libro con ID {} non trovato per prestito id={}", bookId, loan.getId());
                    return null; // Ritorna null, verrà filtrato dopo
                }
            } catch (DAOException e) {
                logger.warn("Impossibile recuperare il libro con ID {} per prestito id={}", bookId, loan.getId(), e);
                return null; // Ritorna null, verrà filtrato dopo
            }
            return bean;
        } catch (Exception e) {
            logger.error("Errore nella conversione del prestito id={}", loan != null ? loan.getId() : "null", e);
            return null;
        }
    }
}