package controller.gui;

import app.state.StateManager;
import controller.app.InfoController;

public class InfoControllerGUI {
    private StateManager stateManager; // Aggiungi questo campo

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager; // Salva lo stateManager
        new InfoController(); // Se serve
    }
}