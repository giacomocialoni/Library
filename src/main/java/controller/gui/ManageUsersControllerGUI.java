package controller.gui;

import app.state.StateManager;
import app.state.ErrorState;
import app.state.SuccessState;
import controller.app.ManageUsersController;
import exception.DAOException;
import exception.RecordNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.User;
import view.components.ManageUsersCardFactory;

import java.util.List;

public class ManageUsersControllerGUI {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private VBox resultsContainer;
    @FXML private Label resultsLabel;

    private StateManager stateManager;
    private final ManageUsersController appController = new ManageUsersController();
    private ManageUsersCardFactory cardFactory;
    
    private boolean initialized = false;

    public void setStateManager(StateManager stateManager) throws RecordNotFoundException, DAOException {
        this.stateManager = stateManager;
        this.cardFactory = new ManageUsersCardFactory();
        
        if (initialized) {
            loadUsers();
        }
    }

    @FXML
    public void initialize() {
        initialized = true;
    }

    @FXML
    public void handleClearFilters() {
        searchField.clear();
        loadUsers();
    }

    public void loadUsers() {
        if (cardFactory == null) return;

        try {
            List<User> users = appController.getLoggedUsers(); // Usa getLoggedUsers invece di getAllUsers
            displayUsers(users);
        } catch (Exception e) {
            showError("Errore", "Errore nel caricamento utenti: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        if (cardFactory == null) {
            showError("Attenzione", "Sistema non ancora inizializzato");
            return;
        }

        String searchText = searchField.getText().trim();
        List<User> users;

        if (searchText.isEmpty()) {
            users = appController.getLoggedUsers(); // Usa getLoggedUsers
        } else {
            users = appController.searchUsers(searchText);
        }

        displayUsers(users);
    }

    private void displayUsers(List<User> users) {
        resultsContainer.getChildren().clear();

        for (User user : users) {
            var userCard = cardFactory.createUserCard(
                user,
                () -> handleRemoveUser(user.getEmail())
            );
            resultsContainer.getChildren().add(userCard);
        }

        updateResultsLabel(users.size());
    }

    private void updateResultsLabel(int count) {
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
            resultsLabel.setText("Totale utenti: " + count);
        } else {
            resultsLabel.setText("Trovati " + count + " utenti per '" + searchText + "'");
        }
    }

    // ===== GESTIONE AZIONI =====

    private void handleRemoveUser(String email) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma eliminazione");
            alert.setHeaderText("Eliminare l'utente?");
            alert.setContentText("Questa operazione cancellerà anche tutte le prenotazioni dell'utente.\nL'operazione non può essere annullata.");
            
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                boolean success = appController.deleteUser(email);
                if (success) {
                    loadUsers();
                    showSuccess("Successo", "Utente eliminato con successo");
                } else {
                    showError("Errore", "Impossibile eliminare l'utente");
                }
            }
        } catch (Exception e) {
            showError("Errore", "Errore nell'eliminare l'utente: " + e.getMessage());
        }
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