package controller.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Account;

import java.util.Map;

import app.Session;
import app.state.BachecaState;
import app.state.CatalogoState;
import app.state.CercaState;
import app.state.ProfiloState;

public class MainUserControllerGUI extends AbstractMainControllerGUI {

    @FXML private Button catalogoButton, cercaButton, bachecaButton, profileButton;
    @FXML private Label userLabel;

    @FXML
    public void initialize() {
        stateButtonMap = Map.of(
            CatalogoState.class, catalogoButton,
            CercaState.class, cercaButton,
            BachecaState.class, bachecaButton,
            ProfiloState.class, profileButton
        );
        updateUserLabel();
    }

    @FXML
    private void showCatalogo() {
        stateManager.setState(new CatalogoState(stateManager));
    }

    @FXML
    private void showCerca() {
        stateManager.setState(new CercaState(stateManager));
    }

    @FXML
    private void showBacheca() {
        stateManager.setState(new BachecaState(stateManager));
    }

    @FXML
    private void showProfile() {
        stateManager.setState(new ProfiloState(stateManager));
    }

    private void updateUserLabel() {
        if (userLabel != null && Session.getInstance().isLoggedIn()) {
            Account loggedUser = Session.getInstance().getLoggedUser();
            userLabel.setText(loggedUser.getFirstName());
        } else if (userLabel != null) {
            userLabel.setText("Guest");
        }
    }
}