package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import utils.LoanStatus;

public class Loan {
    private final int id;
    private final String userEmail;
    private final int bookId; // solo FK nel modello
    private final LocalDate reservedDate;
    private final LocalDate loanedDate;
    private final LocalDate returningDate;
    private final LoanStatus status;

    public Loan(int id, String userEmail, int bookId, LocalDate reservedDate, LocalDate loanedDate, LocalDate returningDate, LoanStatus status) {
        this.id = id;
        this.userEmail = userEmail;
        this.bookId = bookId;
        this.reservedDate = reservedDate;
        this.loanedDate = loanedDate;
        this.returningDate = returningDate;
        this.status = status;
    }

    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public int getBookId() { return bookId; }
    public LocalDate getReservedDate() { return reservedDate; }
    public LocalDate getLoanedDate() { return loanedDate; }
    public LocalDate getReturningDate() { return returningDate; }
    public LoanStatus getStatus() { return status; }

    // Metodi utility
    public boolean isReturned() { return status == LoanStatus.RETURNED; }
    public boolean isExpired() {
        return returningDate != null && LocalDate.now().isAfter(returningDate);
    }
    public long daysRemaining() {
        return returningDate != null ? ChronoUnit.DAYS.between(LocalDate.now(), returningDate) : -1;
    }
}