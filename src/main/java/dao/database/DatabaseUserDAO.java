package dao.database;

import dao.UserDAO;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUserDAO implements UserDAO {

    private final DBConnection dbConnection;

    public DatabaseUserDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT email, password, first_name, last_name FROM users WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT email, password, first_name, last_name FROM users ORDER BY email";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Metodo aggiuntivo utile per la ricerca
    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT email, password, first_name, last_name FROM users " +
                    "WHERE LOWER(email) LIKE LOWER(?) OR LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?) " +
                    "ORDER BY email";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public boolean deleteUser(String email) {
        // IMPORTANTE: Prima elimina le dipendenze (purchases, loans)
        String deletePurchasesSql = "DELETE FROM purchases WHERE user_email = ?";
        String deleteLoansSql = "DELETE FROM loans WHERE user_email = ?";
        String deleteUserSql = "DELETE FROM users WHERE email = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Inizia transazione
            
            // Elimina purchases
            try (PreparedStatement stmt = conn.prepareStatement(deletePurchasesSql)) {
                stmt.setString(1, email);
                stmt.executeUpdate();
            }
            
            // Elimina loans
            try (PreparedStatement stmt = conn.prepareStatement(deleteLoansSql)) {
                stmt.setString(1, email);
                stmt.executeUpdate();
            }
            
            // Elimina user
            try (PreparedStatement stmt = conn.prepareStatement(deleteUserSql)) {
                stmt.setString(1, email);
                int rowsAffected = stmt.executeUpdate();
                
                conn.commit(); // Conferma transazione
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in caso di errore
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}