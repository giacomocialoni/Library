package controller.app;

import dao.BookDAO;
import dao.CategoryDAO;
import dao.factory.DAOFactory;
import model.Book;
import model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CercaController {

    private final BookDAO bookDAO;
    private final CategoryDAO categoryDAO;

    public CercaController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
        this.categoryDAO = DAOFactory.getActiveFactory().getCategoryDAO();
    }

    public List<String> getAllCategoryNames() {
        List<Category> categories = categoryDAO.getAllCategories();
        return categories.stream()
                .map(Category::getCategory)
                .collect(Collectors.toList());
    }
    
    public List<Book> searchBooks(String searchText, String searchMode, String category, String year, boolean includeUnavailable) {
        return bookDAO.getSearchedBooks(searchText, searchMode, category, year, includeUnavailable);
    }
}