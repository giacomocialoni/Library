package controller.gui;

import app.Session;
import app.state.ErrorState;
import app.state.StateManager;
import app.state.SuccessState;
import bean.AccountBean;
import bean.PostBean;
import controller.app.PostController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import view.components.PostCardFactory;

import java.util.List;

public class PostControllerGUI {

    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private Button publishButton;
    @FXML private VBox postsContainer;
    @FXML private Label resultsLabel;

    private StateManager stateManager;
    private final PostController appController = new PostController();
    private PostCardFactory cardFactory;

    private boolean initialized = false;

    // ================== INIT ==================
    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.cardFactory = new PostCardFactory();

        if (initialized) {
            loadPosts();
        }
    }

    @FXML
    public void initialize() {
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(4);

        titleField.textProperty().addListener((obs, oldVal, newVal) -> updatePublishButton());
        contentArea.textProperty().addListener((obs, oldVal, newVal) -> updatePublishButton());

        initialized = true;
    }

    // ================== ACTIONS ==================
    @FXML
    public void handlePublish() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            showError("Il titolo è obbligatorio");
            return;
        }
        if (content.isEmpty()) {
            showError("Il contenuto è obbligatorio");
            return;
        }

        // Recupera il model dalla session e lo mappa in bean tramite il controller applicativo
        AccountBean currentUser = PostController.accountToBean(Session.getInstance().getLoggedUser());
        if (currentUser == null) {
            showError("Utente non autenticato o dati account non validi");
            return;
        }

        boolean success = appController.createPost(currentUser, title, content);

        if (success) {
            titleField.clear();
            contentArea.clear();
            loadPosts();
            showSuccess("Post pubblicato con successo");
        } else {
            showError("Errore durante la pubblicazione del post");
        }
    }

    // ================== LOAD & DISPLAY ==================
    public void loadPosts() {
        if (cardFactory == null) return;

        List<PostBean> posts = appController.getAllPostsOrderedByDate();
        displayPosts(posts);
    }

    private void displayPosts(List<PostBean> posts) {
        postsContainer.getChildren().clear();
        for (PostBean post : posts) {
            postsContainer.getChildren().add(cardFactory.createPostCard(post));
        }
        resultsLabel.setText("Post pubblicati: " + posts.size());
    }

    // ================== UI HELPERS ==================
    private void updatePublishButton() {
        boolean enabled = !titleField.getText().trim().isEmpty()
                && !contentArea.getText().trim().isEmpty();
        publishButton.setDisable(!enabled);
    }

    // ================== STATE MANAGER ==================
    private void showSuccess(String message) {
        stateManager.setState(new SuccessState(stateManager, message));
    }

    private void showError(String message) {
        stateManager.setState(new ErrorState(stateManager, message));
    }
}