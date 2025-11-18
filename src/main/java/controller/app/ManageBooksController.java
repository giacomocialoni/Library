package controller.app;

import dao.BookDAO;
import dao.factory.DAOFactory;
import model.Book;

import java.util.List;
import java.util.stream.Collectors;

public class ManageBooksController {

    private final BookDAO bookDAO;

    public ManageBooksController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    }

    // ===== RICERCHE LIBRI =====

    public List<Book> searchBooks(String searchText) {
        List<Book> allBooks = bookDAO.getAllBooks();
        if (searchText == null || searchText.trim().isEmpty()) {
            return allBooks;
        }

        String finalSearchText = searchText.toLowerCase();
        return allBooks.stream()
                .filter(book -> 
                        book.getTitle().toLowerCase().contains(finalSearchText) ||
                        book.getAuthor().toLowerCase().contains(finalSearchText) ||
                        book.getIsbn().toLowerCase().contains(finalSearchText) ||
                        book.getCategory().toLowerCase().contains(finalSearchText))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByTitle(String searchText) {
        return bookDAO.getAllBooks().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByAuthor(String searchText) {
        return bookDAO.getAllBooks().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ===== OPERAZIONI CRUD =====

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public void addBook(Book book) {
        bookDAO.addBook(book);
    }

    public void updateBook(Book book) {
        bookDAO.updateBook(book);
    }

    public void deleteBook(int bookId) {
        bookDAO.deleteBook(bookId);
    }

    // ===== GESTIONE STOCK =====

    public void increaseStock(int bookId, int quantity) {
        Book book = bookDAO.getBookById(bookId);
        if (book != null) {
            book.setStock(book.getStock() + quantity);
            bookDAO.updateBook(book);
        }
    }

    public void decreaseStock(int bookId, int quantity) {
        Book book = bookDAO.getBookById(bookId);
        if (book != null) {
            int newStock = Math.max(0, book.getStock() - quantity);
            book.setStock(newStock);
            bookDAO.updateBook(book);
        }
    }

    public void setStock(int bookId, int newStock) {
        Book book = bookDAO.getBookById(bookId);
        if (book != null) {
            book.setStock(Math.max(0, newStock));
            bookDAO.updateBook(book);
        }
    }

    // ===== METODI UTILI =====

    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }

    public int getTotalBooksCount() {
        return bookDAO.getAllBooks().size();
    }

    public int getAvailableBooksCount() {
        return (int) bookDAO.getAllBooks().stream()
                .filter(book -> book.getStock() > 0)
                .count();
    }
}