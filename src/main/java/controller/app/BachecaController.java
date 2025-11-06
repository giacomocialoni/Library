package controller.app;

import java.util.List;
import dao.PostDAO;
import dao.factory.DAOFactory;
import model.Post;

public class BachecaController {

    private final PostDAO postDAO;

    public BachecaController() {
        this.postDAO = DAOFactory.getActiveFactory().getPostDAO();
    }

    public List<Post> getAllPostsOrderedByDate() {
        return postDAO.getAllPostsOrderedByDate();
    }

    public void createPost(String adminUsername, String title, String text, String imagePath) {
        //TODO Crea post in bacheca
    }

    public void deletePost(int postId) {
        //TODO Elimina pos in bacheca
    }
}