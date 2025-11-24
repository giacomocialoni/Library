package controller.app;

import dao.BookDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ManageBooksController {

    private static final Logger logger = LoggerFactory.getLogger(ManageBooksController.class);

    private final BookDAO bookDAO;

    public ManageBooksController() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    }

    // ===== RICERCHE LIBRI =====

    public List<Book> searchBooks(String searchText) {
        try {
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

        } catch (DAOException e) {
            logger.error("Errore DAO durante la ricerca dei libri", e);
            return List.of();
        }
    }

    public List<Book> searchBooksByTitle(String searchText) {
        try {
            return bookDAO.getAllBooks().stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (DAOException e) {
            logger.error("Errore DAO durante la ricerca libri per titolo", e);
            return List.of();
        }
    }

    public List<Book> searchBooksByAuthor(String searchText) {
        try {
            return bookDAO.getAllBooks().stream()
                    .filter(book -> book.getAuthor().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (DAOException e) {
            logger.error("Errore DAO durante la ricerca libri per autore", e);
            return List.of();
        }
    }

    // ===== OPERAZIONI CRUD =====

    public List<Book> getAllBooks() {
        try {
            return bookDAO.getAllBooks();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero di tutti i libri", e);
            return List.of();
        }
    }

    public void addBook(Book book) {
        try {
            bookDAO.addBook(book);
        } catch (DAOException e) {
            logger.error("Errore DAO durante l'aggiunta del libro: " + book.getTitle(), e);
        }
    }

    public void updateBook(Book book) {
        try {
            bookDAO.updateBook(book);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro da aggiornare non trovato: " + book.getId(), e);
        } catch (DAOException e) {
            logger.error("Errore DAO durante l'aggiornamento del libro: " + book.getId(), e);
        }
    }

    public void deleteBook(int bookId) {
        try {
            bookDAO.deleteBook(bookId);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro da eliminare non trovato: " + bookId, e);
        } catch (DAOException e) {
            logger.error("Errore DAO durante l'eliminazione del libro: " + bookId, e);
        }
    }

    // ===== GESTIONE STOCK =====

    public void increaseStock(int bookId, int quantity) {
        try {
            Book book = bookDAO.getBookById(bookId);
            book.setStock(book.getStock() + quantity);
            bookDAO.updateBook(book);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro da aumentare stock non trovato: " + bookId, e);
        } catch (DAOException e) {
            logger.error("Errore DAO durante l'aumento stock libro: " + bookId, e);
        }
    }

    public void decreaseStock(int bookId, int quantity) {
        try {
            Book book = bookDAO.getBookById(bookId);
            int newStock = Math.max(0, book.getStock() - quantity);
            book.setStock(newStock);
            bookDAO.updateBook(book);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro da diminuire stock non trovato: " + bookId, e);
        } catch (DAOException e) {
            logger.error("Errore DAO durante la diminuzione stock libro: " + bookId, e);
        }
    }

    public void setStock(int bookId, int newStock) {
        try {
            Book book = bookDAO.getBookById(bookId);
            book.setStock(Math.max(0, newStock));
            bookDAO.updateBook(book);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro da impostare stock non trovato: " + bookId, e);
        } catch (DAOException e) {
            logger.error("Errore DAO durante l'impostazione stock libro: " + bookId, e);
        }
    }

    // ===== METODI UTILI =====

    public Book getBookById(int bookId) {
        try {
            return bookDAO.getBookById(bookId);
        } catch (RecordNotFoundException e) {
            logger.warn("Libro non trovato: " + bookId, e);
            return null;
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero libro: " + bookId, e);
            return null;
        }
    }

    public int getTotalBooksCount() {
        try {
            return bookDAO.getAllBooks().size();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il conteggio totale libri", e);
            return 0;
        }
    }

    public int getAvailableBooksCount() {
        try {
            return (int) bookDAO.getAllBooks().stream()
                    .filter(book -> book.getStock() > 0)
                    .count();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il conteggio libri disponibili", e);
            return 0;
        }
    }
}