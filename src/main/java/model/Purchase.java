package model;

import java.time.LocalDate;
import utils.PurchaseStatus;

public class Purchase {
    private final int id;
    private final String userEmail;
    private final int bookId;
    private final LocalDate statusDate;
    private final PurchaseStatus status;

    public Purchase(int id, String userEmail, int bookId, LocalDate statusDate, PurchaseStatus status) {
        this.id = id;
        this.userEmail = userEmail;
        this.bookId = bookId;
        this.statusDate = statusDate;
        this.status = status;
    }

    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public int getBookId() { return bookId; }
    public LocalDate getStatusDate() { return statusDate; }
    public PurchaseStatus getStatus() { return status; }

    // Metodi utility
    public boolean isPurchased() { return status == PurchaseStatus.PURCHASED; }
    public boolean isReserved() { return status == PurchaseStatus.RESERVED; }
}