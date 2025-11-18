package dao.database;

import model.Book;
import model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.LoanDAO;
import utils.Constants;
import utils.LoanStatus;

public class DatabaseLoanDAO implements LoanDAO {

    private final DBConnection dbConnection;

    public DatabaseLoanDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
	
    @Override
    public List<Loan> getActiveLoansByUser(String userEmail) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id as loan_id, l.user_email, l.status, l.reserved_date, l.loaned_date, l.returning_date, " +
                    "b.id as book_id, b.title, b.author, b.category, b.year, b.publisher, b.pages, b.isbn, b.stock, b.plot, b.image_path, b.price " +
                    "FROM loans l JOIN books b ON l.book_id = b.id " +
                    "WHERE l.user_email = ? AND l.status IN ('LOANED', 'EXPIRED')";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Loan loan = extractLoanFromResultSet(rs);
                loans.add(loan);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }
    
    @Override
	public List<Loan> getReservedLoansByUser(String userEmail) {
		List<Loan> loans = new ArrayList<>();
		String sql = "SELECT l.*, b.* FROM loans l JOIN books b ON l.book_id = b.id WHERE l.user_email = ? AND l.status IN ('RESERVED')";
		
		try (Connection conn = dbConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setString(1, userEmail);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Loan loan = extractLoanFromResultSet(rs);
				loans.add(loan);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return loans;
	}

    @Override
    public void addLoan(String userEmail, int bookId) {
        String sql = "INSERT INTO loans l (user_email, book_id, reserved_date) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public void acceptedLoan(int loanId) {
        String sql = "UPDATE loans l SET status = 'LOANED', loaned_date = ?, returning_date = ? WHERE l.id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
        	LocalDate today = LocalDate.now();
        	LocalDate returningDate = LocalDate.now().plusDays(Constants.LOANING_DAYS);
        	
            stmt.setDate(1, java.sql.Date.valueOf(today));
            stmt.setDate(2, java.sql.Date.valueOf(returningDate));
            stmt.setInt(3, loanId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void returnLoan(int loanId) {
        String sql = "UPDATE loans l SET status = 'RETURNED' WHERE l.id = ?";
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
                Loan loan = extractLoanFromResultSet(rs);
                loans.add(loan);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }
    
    @Override
    public List<Loan> getAllReservedLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.* FROM loans l JOIN books b ON l.book_id = b.id WHERE l.status = 'RESERVED'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) loans.add(extractLoanFromResultSet(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return loans;
    }

    @Override
    public List<Loan> searchLoans(String searchText) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.* FROM loans l JOIN books b ON l.book_id = b.id "
                   + "JOIN users u ON l.user_email = u.email "
                   + "WHERE LOWER(u.email) LIKE ? OR LOWER(u.first_name) LIKE ? OR LOWER(u.last_name) LIKE ? OR LOWER(b.title) LIKE ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + (searchText == null ? "" : searchText.toLowerCase()) + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) loans.add(extractLoanFromResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return loans;
    }

    private Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {

        int loanId;

        try {
            loanId = rs.getInt("loan_id"); // se la query usa alias
        } catch (SQLException e) {
            loanId = rs.getInt("id"); // se la query ha l.*
        }

        String userEmail = rs.getString("user_email");

        // estrai Book
        Book book = new Book(
                rs.getInt("book_id"),
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

        LoanStatus status = LoanStatus.valueOf(rs.getString("status"));

        LocalDate reservedDate    = rs.getDate("reserved_date") != null ? rs.getDate("reserved_date").toLocalDate() : null;
        LocalDate loanedDate      = rs.getDate("loaned_date") != null ? rs.getDate("loaned_date").toLocalDate() : null;
        LocalDate returningDate   = rs.getDate("returning_date") != null ? rs.getDate("returning_date").toLocalDate() : null;

        return new Loan(loanId, userEmail, book, status, reservedDate, loanedDate, returningDate);
    }
}