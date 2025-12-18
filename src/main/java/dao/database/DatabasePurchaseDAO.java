package dao.database;

import dao.PurchaseDAO;
import model.Purchase;
import utils.PurchaseStatus;
import exception.DAOException;
import exception.RecordNotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabasePurchaseDAO implements PurchaseDAO {

    private final DBConnection dbConnection;

    public DatabasePurchaseDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public void addReservedPurchase(String userEmail, int bookId) throws DAOException {
        String sql = "INSERT INTO purchases (user_email, book_id) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiunta di una purchase riservata", e);
        }
    }

    @Override
    public List<Purchase> getPurchasesByUser(String userEmail) throws DAOException {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM purchases WHERE user_email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) purchases.add(extractPurchaseFromResultSet(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero delle purchases per l'utente", e);
        }
        return purchases;
    }

    @Override
    public List<Integer> getPurchasedBookIdsByUser(String userEmail) throws DAOException {
        List<Integer> bookIds = new ArrayList<>();
        String sql = "SELECT book_id FROM purchases WHERE user_email = ? AND status = 'PURCHASED'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) bookIds.add(rs.getInt("book_id"));
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero degli ID libri acquistati dall'utente", e);
        }
        return bookIds;
    }

    @Override
    public boolean hasUserPurchasedBook(String userEmail, int bookId) throws DAOException {
        String sql = "SELECT 1 FROM purchases WHERE user_email = ? AND book_id = ? AND status = 'PURCHASED'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new RecordNotFoundException(
                        "L'utente " + userEmail + " non ha acquistato il libro con ID " + bookId
                    );
                }
                return true;
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il controllo dell'acquisto del libro da parte dell'utente", e);
        }
    }

    @Override
    public void acceptPurchase(int purchaseId) throws DAOException, RecordNotFoundException {
        String sql = "UPDATE purchases SET status = 'PURCHASED' WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, purchaseId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RecordNotFoundException("Nessuna purchase trovata con ID: " + purchaseId);
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'accettazione della purchase", e);
        }
    }

    @Override
    public List<Purchase> getAllPurchases() throws DAOException {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM purchases";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) purchases.add(extractPurchaseFromResultSet(rs));

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero di tutte le purchases", e);
        }
        return purchases;
    }

    @Override
    public List<Purchase> searchReservedPurchasesByUser(String searchText) throws DAOException {
        List<Purchase> res = new ArrayList<>();
        String sql = """
            SELECT p.*
            FROM purchases p
            JOIN users u ON p.user_email = u.email
            WHERE p.status = 'RESERVED'
              AND (LOWER(u.email) LIKE ?
                   OR LOWER(u.first_name) LIKE ?
                   OR LOWER(u.last_name) LIKE ?)
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + (searchText == null ? "" : searchText.toLowerCase()) + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    res.add(extractPurchaseFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante la ricerca delle purchases riservate per utente", e);
        }

        return res;
    }

    @Override
    public List<Purchase> searchReservedPurchasesByBook(String searchText) throws DAOException {
        List<Purchase> res = new ArrayList<>();
        String sql = """
            SELECT p.*
            FROM purchases p
            JOIN books b ON p.book_id = b.id
            WHERE p.status = 'RESERVED'
              AND (LOWER(b.title) LIKE ? OR LOWER(b.author) LIKE ?)
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + (searchText == null ? "" : searchText.toLowerCase()) + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    res.add(extractPurchaseFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante la ricerca delle purchases riservate per libro", e);
        }

        return res;
    }

    private Purchase extractPurchaseFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String userEmail = rs.getString("user_email");
        int bookId = rs.getInt("book_id");
        LocalDate statusDate = rs.getDate("status_date") != null ? rs.getDate("status_date").toLocalDate() : null;
        PurchaseStatus status = PurchaseStatus.valueOf(rs.getString("status"));
        return new Purchase(id, userEmail, bookId, statusDate, status);
    }
}