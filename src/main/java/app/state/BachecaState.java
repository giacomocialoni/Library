package app.state;

import app.StageManager;
import controller.gui.BachecaControllerGUI;
import controller.gui.MainControllerGUI;

public class BachecaState implements AppState {

    private final StateManager stateManager;

    public BachecaState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
    	// carica la vista nella main view attiva (guest/user)
        BachecaControllerGUI controllerBacheca =
            stateManager.getStageManager().<BachecaControllerGUI>loadContent(StageManager.BACHECA_VIEW);

        if (controllerBacheca != null)
        	controllerBacheca.setStateManager();

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null)
            controllerMain.updateActiveButtonByState();
    }

    @Override
    public void onExit() {
        // eventuali salvataggi o cleanup
    }

    @Override
    public void goBack() {
        stateManager.goBack();
    }
}