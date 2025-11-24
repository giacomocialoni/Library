package controller.app;

import java.util.List;

import dao.BookDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogoController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogoController.class);

    private final BookDAO bookDAO;

    public CatalogoController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    }

    public List<Book> getAllBooks() {
        try {
            return bookDAO.getAllBooks();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero del catalogo libri", e);
            return List.of(); // fallback: lista vuota se errore
        }
    }
}