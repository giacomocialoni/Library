package dao;

import model.Loan;
import java.util.List;

public interface LoanDAO {
	void addLoan(String userEmail, int bookId);
    List<Loan> getActiveLoansByUser(String userEmail);
    void returnLoan(int loanId);
    List<Loan> getLoansByUser(String userEmail);
    void acceptedLoan(int loanId);
	List<Loan> getReservedLoansByUser(String userEmail);
}