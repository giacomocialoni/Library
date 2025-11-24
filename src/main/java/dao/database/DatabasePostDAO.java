package dao.database;

import dao.PostDAO;
import model.Post;
import exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabasePostDAO implements PostDAO {

    private final DBConnection dbConnection;

    public DatabasePostDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Post> getAllPostsOrderedByDate() throws DAOException {
        List<Post> posts = new ArrayList<>();

        String sql = """
            SELECT p.user_fk, p.title, p.content, p.post_date,
                   u.first_name, u.last_name, u.role
            FROM posts p
            JOIN users u ON p.user_fk = u.email
            ORDER BY p.post_date DESC
        """;

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String authorName = rs.getString("first_name") + " " + rs.getString("last_name");
                Post post = new Post(
                        rs.getString("user_fk"),
                        authorName,
                        rs.getString("role"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("post_date").toLocalDateTime()
                );
                posts.add(post);
            }

            return posts;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei post", e);
        }
    }

    @Override
    public void addPost(Post post) throws DAOException {
        String sql = "INSERT INTO posts (user_fk, title, content, post_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getUserEmail());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(post.getPostDate()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiunta del post", e);
        }
    }
}