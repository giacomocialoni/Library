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
        LoanBean bean = new LoanBean();
        bean.setId(loan.getId());
        bean.setUserEmail(loan.getUserEmail());
        bean.setStatus(loan.getStatus());
        bean.setReservedDate(loan.getReservedDate());
        bean.setLoanedDate(loan.getLoanedDate());
        bean.setReturningDate(loan.getReturningDate());

        int bookId = loan.getBookId();
        try {
            Book book = bookDAO.getBookById(bookId);
            BookBean bookBean = toBookBean(book);
            bean.setBook(bookBean);
        } catch (DAOException e) {
            logger.warn("Impossibile recuperare il libro per prestito id={}", loan.getId(), e);
        }
        return bean;
    }
}