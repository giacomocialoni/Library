package app.state;

import app.StageManager;
import controller.gui.CercaControllerGUI;
import controller.gui.MainControllerGUI;

public class CercaState implements AppState {

    private final StateManager stateManager;

    public CercaState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        // carica la vista nella main view attiva (guest/user)
        CercaControllerGUI controllerCerca =
            stateManager.getStageManager().<CercaControllerGUI>loadContent(StageManager.CERCA_VIEW);

        if (controllerCerca != null)
        	controllerCerca.setStateManager(stateManager);

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null)
            controllerMain.updateActiveButtonByState();
    }

    @Override
    public void onExit() {
        // eventuali cleanup se servono
    }

    @Override
    public void goBack() {
        stateManager.goBack();
    }
}