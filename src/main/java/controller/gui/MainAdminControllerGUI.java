package controller.gui;

import java.util.Map;

import app.Session;
import app.state.MainGuestState;
import app.state.ManageBooksState;
import app.state.ManageUsersState;
import app.state.PostState;
import app.state.ReservationState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainAdminControllerGUI extends AbstractMainControllerGUI {

	@FXML private Button prenotazioniButton, gestioneLibriButton, gestioneUtentiButton, postButton, logoutButton;

    @FXML
    public void initialize() {
        stateButtonMap = Map.of(
            ReservationState.class, prenotazioniButton,
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
