package dao;

import model.Purchase;
import exception.DAOException;
import exception.RecordNotFoundException;

import java.util.List;

public interface PurchaseDAO {
    void addReservedPurchase(String userEmail, int bookId) throws DAOException;
    List<Purchase> getPurchasesByUser(String userEmail) throws DAOException, RecordNotFoundException;
    List<Integer> getPurchasedBookIdsByUser(String userEmail) throws DAOException;
    boolean hasUserPurchasedBook(String userEmail, int bookId) throws DAOException, RecordNotFoundException;
    void acceptPurchase(int purchaseId) throws DAOException, RecordNotFoundException;
    List<Purchase> getAllPurchases() throws DAOException;
    List<Purchase> searchReservedPurchasesByUser(String searchText) throws DAOException;
    List<Purchase> searchReservedPurchasesByBook(String searchText) throws DAOException;
}