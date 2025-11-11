package dao.database;

import dao.factory.LoanDAO;
import model.Book;
import model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseLoanDAO implements LoanDAO {

    private final DBConnection dbConnection;

    public DatabaseLoanDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public void addLoan(String userEmail, int bookId, LocalDate loanDate, LocalDate dueDate) {
        String sql = "INSERT INTO loans (user_email, book_id, loan_date, due_date, returned) VALUES (?, ?, ?, ?, false)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(loanDate));
            stmt.setDate(4, Date.valueOf(dueDate));
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Loan> getActiveLoansByUser(String userEmail) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.* FROM loans l JOIN books b ON l.book_id = b.id WHERE l.user_email = ? AND l.returned = false";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = extractBookFromResultSet(rs);
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                LocalDate loanDate = rs.getDate("loan_date").toLocalDate();
                boolean returned = rs.getBoolean("returned");
                
                loans.add(new Loan(book, dueDate, loanDate, returned));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    @Override
    public void returnLoan(int loanId) {
        String sql = "UPDATE loans SET returned = true WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Loan> getLoansByUser(String userEmail) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.* FROM loans l JOIN books b ON l.book_id = b.id WHERE l.user_email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = extractBookFromResultSet(rs);
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                LocalDate loanDate = rs.getDate("loan_date").toLocalDate();
                boolean returned = rs.getBoolean("returned");
                
                loans.add(new Loan(book, dueDate, loanDate, returned));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("category"),
            rs.getInt("year"),
            rs.getString("publisher"),
            rs.getInt("pages"),
            rs.getString("isbn"),
            rs.getInt("stock"),
            rs.getString("plot"),
            rs.getString("image_path"),
            rs.getDouble("price")
        );
    }
}