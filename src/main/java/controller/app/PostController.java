package controller.app;

import dao.PostDAO;
import dao.factory.DAOFactory;
import model.Post;

import java.util.List;

public class PostController {

    private final PostDAO postDAO;

    public PostController() {
        this.postDAO = DAOFactory.getActiveFactory().getPostDAO();
    }

    // ===== OPERAZIONI CRUD =====

    public List<Post> getAllPostsOrderedByDate() {
        return postDAO.getAllPostsOrderedByDate();
    }

    public boolean createPost(String userEmail, String authorName, String role, String title, String content) {
        try {
            if (postDAO instanceof dao.database.DatabasePostDAO) {
                Post post = new Post(userEmail, authorName, role, title, content, java.time.LocalDateTime.now());
                ((dao.database.DatabasePostDAO) postDAO).addPost(post);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== METODI UTILI =====

    public int getTotalPostsCount() {
        return postDAO.getAllPostsOrderedByDate().size();
    }

    public List<Post> getRecentPosts(int limit) {
        List<Post> allPosts = postDAO.getAllPostsOrderedByDate();
        return allPosts.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
}