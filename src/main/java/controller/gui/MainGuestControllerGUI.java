package controller.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.Map;

import app.state.AppState;
import app.state.BachecaState;
import app.state.CatalogoState;
import app.state.CercaState;
import app.state.LoginState;

public class MainGuestControllerGUI extends AbstractMainControllerGUI{

	@FXML private Button catalogoButton, cercaButton, bachecaButton, loginButton;

    private Map<Class<? extends AppState>, Button> stateButtonMap;

    @FXML
    public void initialize() {
        // Qui i bottoni sono già iniettati da FXML
        stateButtonMap = Map.of(
            CatalogoState.class, catalogoButton,
            CercaState.class, cercaButton,
            BachecaState.class, bachecaButton,
            LoginState.class, loginButton
        );
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
        Button active = stateButtonMap.get(stateManager.getCurrentState().getClass());
        if (active != null) active.getStyleClass().add("active");
    }

    private void clearActiveButtons() {
        stateButtonMap.values().forEach(b -> b.getStyleClass().remove("active"));
    }
}