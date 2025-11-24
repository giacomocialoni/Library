package controller.app;

import dao.PostDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostDAO postDAO;

    public PostController() {
        this.postDAO = DAOFactory.getActiveFactory().getPostDAO();
    }

    // ===== OPERAZIONI CRUD =====

    public List<Post> getAllPostsOrderedByDate() {
        try {
            return postDAO.getAllPostsOrderedByDate();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero dei post", e);
            return List.of();
        }
    }

    public boolean createPost(String userEmail, String authorName, String role, String title, String content) {
        try {
            Post post = new Post(userEmail, authorName, role, title, content, java.time.LocalDateTime.now());
            postDAO.addPost(post);
            return true;
        } catch (DAOException e) {
            logger.error("Errore DAO durante la creazione del post", e);
            return false;
        }
    }

    // ===== METODI UTILI =====

    public int getTotalPostsCount() {
        return getAllPostsOrderedByDate().size();
    }

    public List<Post> getRecentPosts(int limit) {
        return getAllPostsOrderedByDate().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}