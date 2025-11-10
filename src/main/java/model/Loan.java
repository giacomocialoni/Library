package model;

import java.time.LocalDate;

public class Loan {
    private final Book book;
    private final LocalDate dueDate;
    private final LocalDate fromDate;

    public Loan(Book book, LocalDate dueDate, LocalDate fromDate) {
        this.book = book;
        this.dueDate = dueDate;
        this.fromDate = dueDate;
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

    public boolean isExpired() {
        return LocalDate.now().isAfter(dueDate);
    }

    public long daysRemaining() {
        return LocalDate.now().until(dueDate).getDays();
    }
}