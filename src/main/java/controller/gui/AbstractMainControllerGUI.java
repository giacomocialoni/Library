package controller.gui;

import app.state.StateManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.util.Map;
import app.state.AppState;


public abstract class AbstractMainControllerGUI implements MainControllerGUI {
    @FXML protected StackPane contentArea;
    protected StateManager stateManager;
    protected Map<Class<? extends AppState>, Button> stateButtonMap;

    @Override
    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void setContent(Node node) {
        contentArea.getChildren().setAll(node);
        if (node != null) {
            node.prefWidth(contentArea.getWidth());
            node.prefHeight(contentArea.getHeight());
        }
    }

    @Override
    public void updateActiveButtonByState() {
        clearActiveButtons();
        if (stateManager != null && stateButtonMap != null) {
            Button active = stateButtonMap.get(stateManager.getCurrentState().getClass());
            if (active != null) active.getStyleClass().add("active");
        }
    }

    protected void clearActiveButtons() {
        if (stateButtonMap != null) {
            stateButtonMap.values().forEach(b -> b.getStyleClass().remove("active"));
        }
    }
}