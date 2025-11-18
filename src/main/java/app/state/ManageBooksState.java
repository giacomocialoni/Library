package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.ManageBooksControllerGUI;

public class ManageBooksState implements AppState {

    private final StateManager stateManager;
    
    public ManageBooksState(StateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    @Override
    public void onEnter() {
        ManageBooksControllerGUI manageBookController =
                stateManager.getStageManager().<ManageBooksControllerGUI>loadContent(StageManager.MANAGE_BOOKS_VIEW);

        if (manageBookController != null) {
            manageBookController.setStateManager(stateManager);
            manageBookController.loadBooks(); // Carica i libri all'entrata
        }

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null)
            controllerMain.updateActiveButtonByState();
    }

    @Override
    public void onExit() {
        // Cleanup se necessario
    }

    @Override
    public void goBack() {
        stateManager.goBack();
    }
}