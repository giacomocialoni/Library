package controller.gui;

import app.state.StateManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class ErrorControllerGUI {

    @FXML private Label titleLabel;
    @FXML private Label errorMessageLabel;
    @FXML private Button okButton;

    private StateManager stateManager;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void setErrorData(String errorMessage) {
        titleLabel.setText("Errore");
        errorMessageLabel.setText(errorMessage);
    }

    @FXML
    private void handleOk() {
        stateManager.goBack();
    }
}