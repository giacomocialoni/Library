package controller.gui;

import app.state.StateManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class SuccessControllerGUI {

    @FXML private Label titleLabel;
    @FXML private Label confirmMessageLabel;
    @FXML private Button okButton;

    private StateManager stateManager;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void setSuccessData(String confirmMessage) {
        titleLabel.setText("Successo");
        confirmMessageLabel.setText(confirmMessage);
    }

    @FXML
    private void handleOk() {
        stateManager.goBackTwoStates();
    }
}
