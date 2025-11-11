package controller.gui;

import java.util.List;

import controller.app.BachecaController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import model.Post;
import view.components.PostCardFactory;

public class BachecaControllerGUI {

    @FXML private VBox postsContainer;

    private BachecaController controllerApp;

    public void setStateManager() {
        this.controllerApp = new BachecaController();
        loadPosts();
    }

    private void loadPosts() {
        postsContainer.getChildren().clear();

        List<Post> posts = controllerApp.getAllPostsOrderedByDate();

        PostCardFactory cardFactory = new PostCardFactory();

        for (Post post : posts) {
            VBox postBox = cardFactory.createPostCard(post);
            postsContainer.getChildren().add(postBox);
        }
    }

    @FXML
    private void initialize() {
        // opzionale: gestisci layout o placeholder vuoto
    }
}