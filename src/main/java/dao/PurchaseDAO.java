package dao;

import model.Purchase;

import java.util.List;

public interface PurchaseDAO {
    void addReservedPurchase(String userEmail, int bookId);
    List<Purchase> getPurchasesByUser(String userEmail);
    List<Integer> getPurchasedBookIdsByUser(String userEmail);
    boolean hasUserPurchasedBook(String userEmail, int bookId);
	void acceptPurchase(int purchaseId);
}