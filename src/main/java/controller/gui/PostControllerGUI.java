package controller.gui;

import app.state.StateManager;
import app.state.ErrorState;
import app.state.SuccessState;
import controller.app.PostController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Post;
import view.components.PostCardFactory;
import model.Account;
import app.Session;

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

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.cardFactory = new PostCardFactory();
        
        if (initialized) {
            loadPosts();
        }
    }

    @FXML
    public void initialize() {
        // Configura il TextArea
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(4);
        
        // Aggiungi listener per abilitare/disabilitare il pulsante pubblica
        titleField.textProperty().addListener((observable, oldValue, newValue) -> updatePublishButton());
        contentArea.textProperty().addListener((observable, oldValue, newValue) -> updatePublishButton());
        
        initialized = true;
    }

    @FXML
    public void handlePublish() {
        if (cardFactory == null) {
            showError("Attenzione", "Sistema non ancora inizializzato");
            return;
        }

        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        // Validazione
        if (title.isEmpty()) {
            showError("Errore", "Il titolo è obbligatorio");
            return;
        }

        if (content.isEmpty()) {
            showError("Errore", "Il contenuto è obbligatorio");
            return;
        }

        // Ottieni informazioni utente corrente
        Account currentUser = Session.getInstance().getLoggedUser();
        if (currentUser == null) {
            showError("Errore", "Utente non autenticato");
            return;
        }

        try {
            boolean success = appController.createPost(
                currentUser.getEmail(),
                currentUser.getFirstName() + " " + currentUser.getLastName(),
                currentUser.getRole(),
                title,
                content
            );

            if (success) {
                // Reset form
                titleField.clear();
                contentArea.clear();
                
                // Ricarica posts
                loadPosts();
                
                showSuccess("Successo", "Post pubblicato con successo!");
            } else {
                showError("Errore", "Errore nella pubblicazione del post");
            }
        } catch (Exception e) {
            showError("Errore", "Errore nella pubblicazione: " + e.getMessage());
        }
    }

    public void loadPosts() {
        if (cardFactory == null) return;

        List<Post> posts = appController.getAllPostsOrderedByDate();
        displayPosts(posts);
    }

    private void displayPosts(List<Post> posts) {
        postsContainer.getChildren().clear();

        for (Post post : posts) {
            var postCard = cardFactory.createPostCard(post);
            postsContainer.getChildren().add(postCard);
        }

        updateResultsLabel(posts.size());
    }

    private void updateResultsLabel(int count) {
        resultsLabel.setText("Post pubblicati: " + count);
    }

    private void updatePublishButton() {
        boolean hasTitle = !titleField.getText().trim().isEmpty();
        boolean hasContent = !contentArea.getText().trim().isEmpty();
        publishButton.setDisable(!hasTitle || !hasContent);
    }

    // ===== GESTIONE STATI =====

    private void showSuccess(String title, String message) {
        SuccessState successState = new SuccessState(stateManager, message);
        stateManager.setState(successState);
    }

    private void showError(String title, String message) {
        ErrorState errorState = new ErrorState(stateManager, message);
        stateManager.setState(errorState);
    }
}