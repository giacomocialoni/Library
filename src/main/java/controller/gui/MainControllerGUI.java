package controller.gui;

public interface MainControllerGUI {
	void setStateManager(app.state.StateManager stateManager);
    void updateActiveButtonByState();
    void setContent(javafx.scene.Node node);
}