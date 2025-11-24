package controller.app;

import java.util.List;
import dao.PostDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BachecaController {

	private static final Logger logger = LoggerFactory.getLogger(BachecaController.class);

    private final PostDAO postDAO;

    public BachecaController() {
        this.postDAO = DAOFactory.getActiveFactory().getPostDAO();
    }

    public List<Post> getAllPostsOrderedByDate() {
        try {
            return postDAO.getAllPostsOrderedByDate();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero dei post", e);
            return List.of();
        }
    }
}