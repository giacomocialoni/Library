package dao.database;

import dao.LoanDAO;
import model.Loan;
import utils.Constants;
import utils.LoanStatus;
import exception.DAOException;
import exception.RecordNotFoundException;

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
    public void addLoan(String userEmail, int bookId) throws DAOException {
        String sql = "INSERT INTO loans (user_email, book_id, reserved_date) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiunta del prestito", e);
        }
    }

    @Override
    public List<Loan> getActiveLoansByUser(String userEmail) throws DAOException {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT 
                l.id AS loan_id, l.user_email, l.book_id, l.status, l.reserved_date, l.loaned_date, l.returning_date
            FROM loans l
            WHERE l.user_email = ? AND l.status IN ('LOANED', 'EXPIRED')
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) loans.add(extractLoanFromResultSet(rs));
            }

            return loans;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei prestiti attivi per l'utente", e);
        }
    }

    @Override
    public List<Loan> getReservedLoansByUser(String userEmail) throws DAOException, RecordNotFoundException {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT 
                l.id AS loan_id, l.user_email, l.book_id, l.status, l.reserved_date, l.loaned_date, l.returning_date
            FROM loans l
            WHERE l.user_email = ? AND l.status = 'RESERVED'
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) loans.add(extractLoanFromResultSet(rs));
            }

            if (loans.isEmpty()) {
                throw new RecordNotFoundException("Nessun prestito riservato trovato per l'utente: " + userEmail);
            }

            return loans;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei prestiti riservati per l'utente", e);
        }
    }

    @Override
    public void acceptedLoan(int loanId) throws DAOException, RecordNotFoundException {
        String sql = "UPDATE loans SET status = 'LOANED', loaned_date = ?, returning_date = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate today = LocalDate.now();
            LocalDate returningDate = today.plusDays(Constants.LOANING_DAYS);

            stmt.setDate(1, java.sql.Date.valueOf(today));
            stmt.setDate(2, java.sql.Date.valueOf(returningDate));
            stmt.setInt(3, loanId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RecordNotFoundException("Nessun prestito trovato con ID: " + loanId);
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'accettazione del prestito", e);
        }
    }

    @Override
    public void returnLoan(int loanId) throws DAOException, RecordNotFoundException {
        String updateLoanSql = "UPDATE loans SET status = 'RETURNED' WHERE id = ?";
        String updateBookStockSql = "UPDATE books SET stock = stock + 1 WHERE id = (SELECT book_id FROM loans WHERE id = ?)";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false); // inizio transazione

            try (PreparedStatement loanStmt = conn.prepareStatement(updateLoanSql);
                 PreparedStatement stockStmt = conn.prepareStatement(updateBookStockSql)) {

                loanStmt.setInt(1, loanId);
                int rowsAffected = loanStmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    throw new RecordNotFoundException("Nessun prestito trovato con ID: " + loanId);
                }

                stockStmt.setInt(1, loanId);
                stockStmt.executeUpdate();

                conn.commit(); // commit della transazione

            } catch (SQLException e) {
                conn.rollback(); // rollback in caso di errore
                throw e;
            } finally {
                conn.setAutoCommit(true); // ripristino modalità auto-commit
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante la restituzione del prestito", e);
        }
    }

    @Override
    public List<Loan> getLoansByUser(String userEmail) throws DAOException {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT 
                l.id AS loan_id, l.user_email, l.book_id, l.status, l.reserved_date, l.loaned_date, l.returning_date
            FROM loans l
            WHERE l.user_email = ?
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) loans.add(extractLoanFromResultSet(rs));
            }

            return loans;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei prestiti dell'utente", e);
        }
    }

    @Override
    public List<Loan> getAllReservedLoans() throws DAOException {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT 
                l.id AS loan_id, l.user_email, l.book_id, l.status, l.reserved_date, l.loaned_date, l.returning_date
            FROM loans l
            WHERE l.status = 'RESERVED'
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) loans.add(extractLoanFromResultSet(rs));
            
            return loans;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero di tutti i prestiti riservati", e);
        }
    }

    @Override
    public List<Loan> searchLoansByUser(String searchText) throws DAOException {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT 
                l.id AS loan_id, l.user_email, l.book_id, l.status, l.reserved_date, l.loaned_date, l.returning_date
            FROM loans l
            JOIN users u ON l.user_email = u.email
            WHERE l.status = 'LOANED' AND (
                LOWER(u.email) LIKE ? OR LOWER(u.first_name) LIKE ? OR LOWER(u.last_name) LIKE ?
            )
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + (searchText == null ? "" : searchText.toLowerCase()) + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) loans.add(extractLoanFromResultSet(rs));
            }

            return loans;

        } catch (SQLException e) {
            throw new DAOException("Errore durante la ricerca dei prestiti LOANED per utente", e);
        }
    }

    @Override
    public List<Loan> searchLoansByBook(String searchText) throws DAOException {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT 
                l.id AS loan_id, l.user_email, l.book_id, l.status, l.reserved_date, l.loaned_date, l.returning_date
            FROM loans l
            JOIN books b ON l.book_id = b.id
            WHERE l.status = 'LOANED' AND LOWER(b.title) LIKE ?
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + (searchText == null ? "" : searchText.toLowerCase()) + "%";
            stmt.setString(1, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) loans.add(extractLoanFromResultSet(rs));
            }

            return loans;

        } catch (SQLException e) {
            throw new DAOException("Errore durante la ricerca dei prestiti LOANED per libro", e);
        }
    }

    @Override
    public List<Loan> getAllLoanedLoans() throws DAOException {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT 
                l.id AS loan_id, l.user_email, l.book_id, l.status, l.reserved_date, l.loaned_date, l.returning_date
            FROM loans l
            WHERE l.status IN ('LOANED', 'EXPIRED')
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }

            return loans;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei prestiti in corso", e);
        }
    }

    private Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        int loanId = rs.getInt("loan_id");
        String userEmail = rs.getString("user_email");
        
        // Ora book_id è SEMPRE presente nelle query
        int bookId = rs.getInt("book_id");
        
        LoanStatus status = LoanStatus.valueOf(rs.getString("status"));

        LocalDate reservedDate = rs.getDate("reserved_date") != null ? rs.getDate("reserved_date").toLocalDate() : null;
        LocalDate loanedDate = rs.getDate("loaned_date") != null ? rs.getDate("loaned_date").toLocalDate() : null;
        LocalDate returningDate = rs.getDate("returning_date") != null ? rs.getDate("returning_date").toLocalDate() : null;

        return new Loan(loanId, userEmail, bookId, reservedDate, loanedDate, returningDate, status);
    }
}