package dao;

import model.Loan;
import exception.DAOException;
import exception.RecordNotFoundException;

import java.util.List;

public interface LoanDAO {
    void addLoan(String userEmail, int bookId) throws DAOException;
    List<Loan> getActiveLoansByUser(String userEmail) throws DAOException, RecordNotFoundException;
    void returnLoan(int loanId) throws DAOException, RecordNotFoundException;
    List<Loan> getLoansByUser(String userEmail) throws DAOException, RecordNotFoundException;
    void acceptedLoan(int loanId) throws DAOException, RecordNotFoundException;
    List<Loan> getReservedLoansByUser(String userEmail) throws DAOException, RecordNotFoundException;
    List<Loan> getAllReservedLoans() throws DAOException;
    List<Loan> searchLoans(String searchText) throws DAOException;
}