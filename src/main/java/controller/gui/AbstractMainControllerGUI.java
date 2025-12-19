package controller.gui;

import app.state.StateManager;
import app.state.AppState;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.util.Map;

public abstract class AbstractMainControllerGUI implements MainControllerGUI {
    @FXML protected StackPane contentArea;
    protected StateManager stateManager;
    protected Map<Class<? extends AppState>, Button> stateButtonMap;
    
    // Aggiunto: riferimento al VBox top per controllare visibilità
    @FXML protected javafx.scene.layout.VBox topContainer;

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
    public void setVisible(boolean showsMain) {
        if (topContainer != null) {
            topContainer.setVisible(showsMain);
            topContainer.setManaged(showsMain); // Importante: non occupa spazio quando invisibile
        }
    }

    @Override
    public void updateActiveButtonByState() {
        clearActiveButtons();
        if (stateManager != null && stateManager.getCurrentState() != null && stateButtonMap != null) {
            AppState currentState = stateManager.getCurrentState();
            Button active = stateButtonMap.get(currentState.getClass());
            if (active != null) {
                active.getStyleClass().add("active");
            }
        }
    }

    protected void clearActiveButtons() {
        if (stateButtonMap != null) {
            stateButtonMap.values().forEach(b -> b.getStyleClass().remove("active"));
        }
    }
}