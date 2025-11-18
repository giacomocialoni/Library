package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.ManageUsersControllerGUI;

public class ManageUsersState implements AppState {

    private final StateManager stateManager;
    
    public ManageUsersState(StateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    @Override
    public void onEnter() {
        ManageUsersControllerGUI manageUsersController =
                stateManager.getStageManager().<ManageUsersControllerGUI>loadContent(StageManager.MANAGE_USERS_VIEW);

        if (manageUsersController != null) {
            manageUsersController.setStateManager(stateManager);
            manageUsersController.loadUsers(); // Carica gli utenti all'entrata
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