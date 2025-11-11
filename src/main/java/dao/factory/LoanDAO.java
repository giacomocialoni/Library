package dao.factory;

import model.Loan;
import java.time.LocalDate;
import java.util.List;

public interface LoanDAO {
    void addLoan(String userEmail, int bookId, LocalDate loanDate, LocalDate dueDate);
    List<Loan> getActiveLoansByUser(String userEmail);
    void returnLoan(int loanId);
    List<Loan> getLoansByUser(String userEmail);
}