package dao.database;

import dao.AccountDAO;
import exception.RecordNotFoundException;
import exception.DuplicateRecordException;
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
    public Account login(String email, String password) throws SQLException, RecordNotFoundException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new RecordNotFoundException("Credenziali non valide");
            }

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
        }
    }

    @Override
    public boolean register(String email, String password, String firstName, String lastName)
            throws SQLException, DuplicateRecordException {

        if (emailExists(email)) {
            throw new DuplicateRecordException("Email già registrata");
        }

        String sql = "INSERT INTO users (email, password, first_name, last_name, role) VALUES (?, ?, ?, ?, 'logged_user')";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);

            return stmt.executeUpdate() > 0;
        }
    }

    private boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}