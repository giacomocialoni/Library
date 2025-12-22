package controller.gui;

import app.state.StateManager;
import app.state.SuccessState;
import app.state.ErrorState;
import controller.app.ManageUsersController;
import view.dto.UserDisplayDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.cardFactory = new ManageUsersCardFactory();
        loadUsers();
    }

    @FXML
    public void initialize() {
        // Inizializzazione FXML
    }

    @FXML
    public void handleClearFilters() {
        searchField.clear();
        loadUsers();
    }

    @FXML
    public void handleSearch() {
        String searchText = searchField.getText().trim();
        
        List<UserDisplayDTO> usersForDisplay;
        if (searchText.isEmpty()) {
            usersForDisplay = appController.getAllUsersForDisplay();
        } else {
            usersForDisplay = appController.searchUsersForDisplay(searchText);
        }
        
        displayUsers(usersForDisplay);
    }

    public void loadUsers() {
        List<UserDisplayDTO> usersForDisplay = appController.getAllUsersForDisplay();
        displayUsers(usersForDisplay);
    }

    private void displayUsers(List<UserDisplayDTO> usersForDisplay) {
        resultsContainer.getChildren().clear();

        for (UserDisplayDTO userDTO : usersForDisplay) {
            var userCard = cardFactory.createUserCard(
                userDTO.getUser(),
                userDTO.getLastPurchaseInfo(),
                userDTO.getLastLoanInfo(),
                userDTO.getStatsInfo(),
                () -> handleRemoveUser(userDTO.getUser().getEmail()) // Rimuove direttamente
            );
            resultsContainer.getChildren().add(userCard);
        }

        updateResultsLabel(usersForDisplay.size());
    }

    private void updateResultsLabel(int count) {
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
            resultsLabel.setText("Totale utenti: " + count);
        } else {
            resultsLabel.setText("Trovati " + count + " utenti per '" + searchText + "'");
        }
    }

    private void handleRemoveUser(String email) {
        try {
            boolean success = appController.deleteUser(email);
            String msg = "L'utente " + email + " è stato eliminato con successo";
            
            if (success) {
                loadUsers();
                stateManager.setState(new SuccessState(stateManager, msg));
            } else {
                stateManager.setState(new ErrorState(stateManager, "Impossibile eliminare l'utente"));
            }

        } catch (Exception e) {
            stateManager.setState(new ErrorState(stateManager, "Errore nell'eliminare l'utente: " + e.getMessage()));
        }
    }
}