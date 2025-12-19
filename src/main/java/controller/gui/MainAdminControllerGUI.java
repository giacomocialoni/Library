package controller.gui;

import java.util.Map;

import app.Session;
import app.state.MainGuestState;
import app.state.ManageBooksState;
import app.state.ManageUsersState;
import app.state.PostState;
import app.state.ReservationState;
import app.state.ReturnLoanState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MainAdminControllerGUI extends AbstractMainControllerGUI {

    @FXML private Button prenotazioniButton, gestioneLibriButton, gestioneUtentiButton, postButton, logoutButton, prestitiButton;
    @FXML private VBox topContainer; // Aggiungi questo campo

    @FXML
    public void initialize() {
        stateButtonMap = Map.of(
            ReservationState.class, prenotazioniButton,
            ReturnLoanState.class, prestitiButton,
            ManageBooksState.class, gestioneLibriButton,
            ManageUsersState.class, gestioneUtentiButton,
            PostState.class, postButton
        );
    }

    @FXML
    private void showReservations() {
        stateManager.setState(new ReservationState(stateManager));
    }
    
    @FXML
    private void showReturnLoans() {
        stateManager.setState(new ReturnLoanState(stateManager));
    }

    @FXML
    private void showManageBooks() {
        stateManager.setState(new ManageBooksState(stateManager));
    }

    @FXML
    private void showManageUsers() {
        stateManager.setState(new ManageUsersState(stateManager));
    }

    @FXML
    private void showPost() {
        stateManager.setState(new PostState(stateManager));
    }

    @FXML
    private void handleLogout() {
        Session.getInstance().logout();
        stateManager.setState(new MainGuestState(stateManager));
    }
}