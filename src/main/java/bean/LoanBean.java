package bean;

import java.time.LocalDate;
import utils.LoanStatus;

public class LoanBean {
    private int id;
    private String userEmail;
    private LoanStatus status;
    private LocalDate reservedDate;
    private LocalDate loanedDate;
    private LocalDate returningDate;
    private BookBean book; // completo per GUI

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public LocalDate getReservedDate() { return reservedDate; }
    public void setReservedDate(LocalDate reservedDate) { this.reservedDate = reservedDate; }
    public LocalDate getLoanedDate() { return loanedDate; }
    public void setLoanedDate(LocalDate loanedDate) { this.loanedDate = loanedDate; }
    public LocalDate getReturningDate() { return returningDate; }
    public void setReturningDate(LocalDate returningDate) { this.returningDate = returningDate; }
    public BookBean getBook() { return book; }
    public void setBook(BookBean book) { this.book = book; }

    public boolean isReturned() { return status == LoanStatus.RETURNED; }
    public boolean isExpired() { return status == LoanStatus.EXPIRED || (returningDate != null && LocalDate.now().isAfter(returningDate)); }
    public long daysRemaining() { return returningDate != null ? java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), returningDate) : -1; }
}