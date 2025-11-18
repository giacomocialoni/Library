package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import utils.LoanStatus;

public class Loan {
    private final int id; // AGGIUNTO
    private final String userEmail; // AGGIUNTO  
    private final Book book;
    private LoanStatus status; // AGGIUNTO - corrisponde al DB
    private final LocalDate reservedDate;
    private final LocalDate loanedDate;
    private final LocalDate returningDate;

    // Costruttore aggiornato
    public Loan(int id, String userEmail, Book book, LoanStatus status, LocalDate reservedDate, LocalDate loanedDate, LocalDate returningDate) {
        this.id = id;
        this.userEmail = userEmail;
        this.book = book;
        this.status = status;
        this.reservedDate = reservedDate;
        this.loanedDate = loanedDate;
        this.returningDate = returningDate;
    }

    // NUOVI GETTER
    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public LoanStatus getStatus() { return status; }
    
    public boolean isReturned() {
        return status == LoanStatus.RETURNED;
    }

    public Book getBook() {
        return book;
    }
    
    public int getBookId() {
    	return book.getId();
    }
    
    public LoanStatus getLoanStatus() {
    	return status;
    }

    public LocalDate getReservedDate() {
        return reservedDate;
    }

    public LocalDate getLoanedDate() {
        return loanedDate;
    }

    public LocalDate getReturningDate() {
        return returningDate;
    }

    public boolean isExpired() {
    	if (returningDate == null) {
            return false; // o un valore di default appropriato
        }
        return LocalDate.now().isAfter(returningDate);
    }

    public long daysRemaining() {
    	if (returningDate == null) {
            return -1; // o un valore di default appropriato
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), returningDate);
    }
}
