package controller.gui;

import app.state.StateManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public abstract class AbstractMainControllerGUI implements MainControllerGUI {
    @FXML protected StackPane contentArea;

    protected StateManager stateManager;

    @Override
    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void setContent(Node node) {
        contentArea.getChildren().setAll(node);
        // Forza il ridimensionamento del contenuto
        if (node != null) {
            node.prefWidth(contentArea.getWidth());
            node.prefHeight(contentArea.getHeight());
        }
    }

    protected void clearActiveButtons(Button... buttons) {
        for (Button b : buttons)
            b.getStyleClass().remove("active");
    }
}