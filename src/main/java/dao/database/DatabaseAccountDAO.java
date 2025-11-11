package dao.database;

import dao.AccountDAO;
import model.Admin;
import model.User;
import model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAccountDAO implements AccountDAO {

    private final DBConnection dbConnection;

    public DatabaseAccountDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Account login(String email, String password) throws Exception {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if ("admin".equalsIgnoreCase(role)) {
                    return new Admin(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                    );
                } else {
                    return new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                    );
                }
            } else {
                return null; // login fallito
            }
        }
    }

    public boolean register(String email, String password, String firstName, String lastName) throws SQLException {
        // Prima verifica se l'email esiste già
        if (emailExists(email)) {
            return false;
        }

        String sql = "INSERT INTO users (email, password, first_name, last_name, role) VALUES (?, ?, ?, ?, 'logged_user')";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Se c'è un risultato, l'email esiste già
        }
    }
}