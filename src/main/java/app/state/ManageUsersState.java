package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.ManageUsersControllerGUI;
import exception.DAOException;
import exception.RecordNotFoundException;

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
            try {
				manageUsersController.setStateManager(stateManager);
			} catch (RecordNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				manageUsersController.loadUsers();
			} catch (RecordNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Carica gli utenti all'entrata
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