package controller.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import app.state.StateManager;
import app.state.BachecaState;
import app.state.CatalogoState;
import app.state.CercaState;
import app.state.LoginState;

public class MainControllerGUI {

	@FXML private StackPane contentArea;
    @FXML private Button catalogoButton, cercaButton, bachecaButton, loginButton;

    private StateManager stateManager;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void setContent(Node node) {
        contentArea.getChildren().setAll(node);
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
    private void showLogin() {
        stateManager.setState(new LoginState(stateManager));
    }

    public void updateActiveButtonByState() {
        clearActiveButtons();

        if (stateManager.getCurrentState() instanceof CatalogoState)
            catalogoButton.getStyleClass().add("active");
        else if (stateManager.getCurrentState() instanceof CercaState)
            cercaButton.getStyleClass().add("active");
        else if (stateManager.getCurrentState() instanceof BachecaState)
            bachecaButton.getStyleClass().add("active");
        else if (stateManager.getCurrentState() instanceof LoginState)
            loginButton.getStyleClass().add("active");
    }

    private void clearActiveButtons() {
        catalogoButton.getStyleClass().remove("active");
        cercaButton.getStyleClass().remove("active");
        bachecaButton.getStyleClass().remove("active");
        loginButton.getStyleClass().remove("active");
    }
}