package dao.database;

import dao.PurchaseDAO;
import model.Purchase;
import utils.PurchaseStatus;

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
    public void addReservedPurchase(String userEmail, int bookId) {
        String sql = "INSERT INTO purchases (user_email, book_id) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Purchase> getPurchasesByUser(String userEmail) {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM purchases WHERE user_email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Purchase purchase = extractPurchaseFromResultSet(rs);
                purchases.add(purchase);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    @Override
    public List<Integer> getPurchasedBookIdsByUser(String userEmail) {
        List<Integer> bookIds = new ArrayList<>();
        String sql = "SELECT book_id FROM purchases WHERE user_email = ? AND status = 'PURCHASED'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookIds.add(rs.getInt("book_id"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookIds;
    }
    
    @Override
    public boolean hasUserPurchasedBook(String userEmail, int bookId) {
        String sql = "SELECT 1 FROM purchases WHERE user_email = ? AND book_id = ? AND status = 'PURCHASED'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void acceptPurchase(int purchaseId) {
        String sql = "UPDATE purchases SET status = 'PURCHASED' WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, purchaseId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Purchase extractPurchaseFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String userEmail = rs.getString("user_email");
        int bookId = rs.getInt("book_id");
        LocalDate purchaseStatus = rs.getDate("status_date") != null ? 
            rs.getDate("status_date").toLocalDate() : null;
        PurchaseStatus status = PurchaseStatus.valueOf(rs.getString("status"));
        
        return new Purchase(id, userEmail, bookId, purchaseStatus, status);
    }
}