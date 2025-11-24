package dao;

import model.Book;
import model.Loan;
import model.Purchase;

import java.util.List;

import exception.DAOException;
import exception.RecordNotFoundException;

public interface BookDAO {

    List<Book> getAllBooks() throws DAOException;
    Book getBookById(int id) throws DAOException, RecordNotFoundException;
    void addBook(Book book) throws DAOException;
    void updateBook(Book book) throws DAOException, RecordNotFoundException;
    void deleteBook(int id) throws DAOException, RecordNotFoundException;
    List<Loan> getLoanedBooks(String userEmail) throws DAOException;
    List<Book> getSearchedBooks(String searchText, String searchMode, String category, String yearFrom, String yearTo, boolean includeUnavailable) throws DAOException;
    List<Book> getPurchasedBooks(String userEmail) throws DAOException;
    List<Purchase> getPurchasesByUser(String userEmail) throws DAOException;
}