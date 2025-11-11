package model;

import java.time.LocalDate;

public class Loan {
    private final Book book;
    private final LocalDate dueDate;
    private final LocalDate fromDate;
    private final boolean returned;


    public Loan(Book book, LocalDate dueDate, LocalDate fromDate, boolean returned) {
        this.book = book;
        this.dueDate = dueDate;
        this.fromDate = fromDate;
        this.returned = returned;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public boolean isReturned() {
    	return returned;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(dueDate);
    }

    public long daysRemaining() {
        return LocalDate.now().until(dueDate).getDays();
    }
}