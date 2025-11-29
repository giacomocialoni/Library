package dao.database;

import dao.WishlistDAO;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.User;
import model.Wishlist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseWishlistDAO implements WishlistDAO {

    private final DBConnection dbConnection;

    public DatabaseWishlistDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public void addToWishlist(String userEmail, int bookId) throws DAOException {
        String sql = "INSERT INTO wishlist (user_email, book_id) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiunta alla wishlist", e);
        }
    }

    @Override
    public void removeFromWishlist(String userEmail, int bookId) throws DAOException {
        String sql = "DELETE FROM wishlist WHERE user_email = ? AND book_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante la rimozione dalla wishlist", e);
        }
    }

    @Override
    public boolean isInWishlist(String userEmail, int bookId) throws DAOException {
        String sql = "SELECT 1 FROM wishlist WHERE user_email = ? AND book_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il controllo della wishlist", e);
        }
    }

    @Override
    public List<Wishlist> getWishlistByUser(String userEmail) throws DAOException, RecordNotFoundException {
        List<Wishlist> wishlist = new ArrayList<>();
        String sql = "SELECT * FROM wishlist WHERE user_email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    wishlist.add(new Wishlist(rs.getString("user_email"), rs.getInt("book_id")));
                }
            }


        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero della wishlist", e);
        }

        return wishlist;
    }

    @Override
    public List<User> getUsersWithBookInWishlist(int bookId) throws DAOException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM wishlist w JOIN users u ON w.user_email = u.email WHERE w.book_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                    );
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero degli utenti che hanno il libro nella wishlist", e);
        }

        return users;
    }
}