// DatabasePurchaseDAO.java - MODIFICA
package dao.database;

import dao.factory.PurchaseDAO;

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
    public void addPurchase(String userEmail, int bookId, LocalDate purchaseDate) {
        // Prima verifica se esiste già un record per questo utente-libro
        if (!purchaseExists(userEmail, bookId)) {
            String sql = "INSERT INTO purchases (user_email, book_id, purchase_date) VALUES (?, ?, ?)";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, userEmail);
                stmt.setInt(2, bookId);
                stmt.setDate(3, Date.valueOf(purchaseDate));
                stmt.executeUpdate();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Se esiste già, non fa nulla (oppure puoi aggiornare la data se preferisci)
    }

    private boolean purchaseExists(String userEmail, int bookId) {
        String sql = "SELECT 1 FROM purchases WHERE user_email = ? AND book_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Restituisce true se esiste già
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Integer> getPurchasedBookIdsByUser(String userEmail) {
        List<Integer> bookIds = new ArrayList<>();
        String sql = "SELECT book_id FROM purchases WHERE user_email = ?";
        
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
        String sql = "SELECT 1 FROM purchases WHERE user_email = ? AND book_id = ?";
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
}