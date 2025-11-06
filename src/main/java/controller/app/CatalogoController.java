package controller.app;

import java.util.List;

import dao.BookDAO;
import dao.factory.DAOFactory;
import model.Book;

public class CatalogoController {

	private final BookDAO bookDAO;

    public CatalogoController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }
}
