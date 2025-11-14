package dao;

import model.Book;
import model.Loan;
import model.Purchase;

import java.util.List;

public interface BookDAO {
    List<Book> getAllBooks();
    Book getBookById(int id);
    void addBook(Book book);
    void updateBook(Book book);
    void deleteBook(int id);
	List<Loan> getLoanedBooks(String userEmail);
    List<Book> getSearchedBooks(String searchText, String searchMode, String category, String yearFrom, String yearTo, boolean includeUnavailable);
    List<Book> getPurchasedBooks(String userEmail); // Restituisce List<Book>
    List<Purchase> getPurchasesByUser(String userEmail); // NUOVO: restituisce List<Purchase>
}