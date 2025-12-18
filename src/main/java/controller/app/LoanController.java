package controller.app;

import app.Session;
import bean.BookBean;
import bean.LoanBean;
import dao.BookDAO;
import dao.LoanDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.IncorrectDataException;
import exception.RecordNotFoundException;
import model.Account;
import model.Book;
import model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.LoanResult;

import java.util.List;
import java.util.stream.Collectors;

public class LoanController {

    private static final Logger logger =
            LoggerFactory.getLogger(LoanController.class);

    private final BookDAO bookDAO;
    private final LoanDAO loanDAO;

    public LoanController() {
        DAOFactory factory = DAOFactory.getActiveFactory();
        this.bookDAO = factory.getBookDAO();
        this.loanDAO = factory.getLoanDAO();
    }

    // Loan

    public LoanResult loanBook(int bookId) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn())
            return LoanResult.NOT_LOGGED;

        Account user = session.getLoggedUser();
        Book book;

        try {
            book = bookDAO.getBookById(bookId);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro non trovato id={}", bookId);
            return LoanResult.ERROR;
        } catch (DAOException e) {
            logger.error("Errore DAO nel recupero libro id={}", bookId, e);
            return LoanResult.ERROR;
        }

        if (hasExpiredLoans(user.getEmail()))
            return LoanResult.EXPIRED_LOAN_EXISTS;

        if (getActiveLoansCount(user.getEmail()) >= Constants.MAX_ACTIVE_LOANS)
            return LoanResult.MAX_LOANS_REACHED;

        if (book.getStock() <= 0)
            return LoanResult.INSUFFICIENT_STOCK;

        try {
            book.setStock(book.getStock() - 1);
            bookDAO.updateBook(book);
            loanDAO.addLoan(user.getEmail(), bookId);
            return LoanResult.SUCCESS;

        } catch (RecordNotFoundException e) {
            logger.warn("Libro non trovato durante update id={}", bookId);
            return LoanResult.ERROR;
        } catch (DAOException e) {
            logger.error(
                "Errore DAO durante prestito libro id={} user={}",
                bookId, user.getEmail(), e
            );
            return LoanResult.ERROR;
        }
    }

    public boolean returnBook(int loanId) {
        try {
            loanDAO.returnLoan(loanId);
            return true;
        } catch (RecordNotFoundException e) {
            logger.warn("Prestito non trovato id={}", loanId);
            return false;
        } catch (DAOException e) {
            logger.error("Errore DAO durante reso prestito id={}", loanId, e);
            return false;
        }
    }

    // Bean

    public List<LoanBean> getUserActiveLoans() {
        Session session = Session.getInstance();
        if (!session.isLoggedIn())
            return List.of();

        try {
            return loanDAO
                    .getActiveLoansByUser(session.getLoggedUser().getEmail())
                    .stream()
                    .map(this::toLoanBean)
                    .collect(Collectors.toList());

        } catch (RecordNotFoundException e) {
            return List.of();
        } catch (DAOException e) {
            logger.error(
                "Errore DAO nel recupero prestiti attivi user={}",
                session.getLoggedUser().getEmail(), e
            );
            return List.of();
        }
    }

    public List<LoanBean> getUserAllLoans() {
        Session session = Session.getInstance();
        if (!session.isLoggedIn())
            return List.of();

        try {
            return loanDAO
                    .getLoansByUser(session.getLoggedUser().getEmail())
                    .stream()
                    .map(this::toLoanBean)
                    .collect(Collectors.toList());

        } catch (RecordNotFoundException e) {
            return List.of();
        } catch (DAOException e) {
            logger.error(
                "Errore DAO nel recupero prestiti user={}",
                session.getLoggedUser().getEmail(), e
            );
            return List.of();
        }
    }

    // Logic

    boolean hasExpiredLoans(String userEmail) {
        try {
            return loanDAO.getActiveLoansByUser(userEmail)
                    .stream()
                    .anyMatch(Loan::isExpired);
        } catch (RecordNotFoundException e) {
            return false;
        } catch (DAOException e) {
            logger.error(
                "Errore DAO nel controllo prestiti scaduti user={}",
                userEmail, e
            );
            return false;
        }
    }

    int getActiveLoansCount(String userEmail) {
        try {
            return loanDAO.getActiveLoansByUser(userEmail).size();
        } catch (RecordNotFoundException e) {
            return 0;
        } catch (DAOException e) {
            logger.error(
                "Errore DAO nel conteggio prestiti attivi user={}",
                userEmail, e
            );
            return 0;
        }
    }

    // Mapping

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
    
    private BookBean toBookBean(Book book) {
        BookBean bean = new BookBean();
        try {
			bean.setId(book.getId());
			bean.setTitle(book.getTitle());
			bean.setAuthor(book.getAuthor());
			bean.setStock(book.getStock());
			bean.setCategory(book.getCategory());
		} catch (IncorrectDataException e) {
			logger.warn("Error Incorrect Data for mapping book.", e);
			return null;
		}
        return bean;
    }
}