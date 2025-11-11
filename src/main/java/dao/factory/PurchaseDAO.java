package dao.factory;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseDAO {
    void addPurchase(String userEmail, int bookId, LocalDate purchaseDate);
    List<Integer> getPurchasedBookIdsByUser(String userEmail);
    boolean hasUserPurchasedBook(String userEmail, int bookId);
}