package controller.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Account;

import java.util.Map;

import app.Session;
import app.state.AppState;
import app.state.BachecaState;
import app.state.CatalogoState;
import app.state.CercaState;
import app.state.ProfiloState;

public class MainUserControllerGUI extends AbstractMainControllerGUI{

	@FXML private Button catalogoButton, cercaButton, bachecaButton, profileButton;
	@FXML Label userLabel;

    private Map<Class<? extends AppState>, Button> stateButtonMap;

    @FXML
    public void initialize() {
        // Qui i bottoni sono già iniettati da FXML
        stateButtonMap = Map.of(
            CatalogoState.class, catalogoButton,
            CercaState.class, cercaButton,
            BachecaState.class, bachecaButton,
            ProfiloState.class, profileButton
        );
        if (Session.getInstance().isLoggedIn() && userLabel != null) {
        	Account loggedUser = Session.getInstance().getLoggedUser();
            userLabel.setText(loggedUser.getFirstName());
            System.out.println("Settato nome: " + loggedUser.getFirstName());
        }
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
    private void showProfile() {
        //TODO Profile State
    	//stateManager.setState(new ProfileState(stateManager));
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