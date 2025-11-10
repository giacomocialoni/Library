package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.ProfiloControllerGUI;

public class ProfiloState implements AppState {

    private final StateManager stateManager;

    public ProfiloState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        // carica la vista del profilo nella main view attiva
        ProfiloControllerGUI controllerProfilo =
            stateManager.getStageManager().<ProfiloControllerGUI>loadContent(StageManager.PROFILO_VIEW);

        if (controllerProfilo != null)
            controllerProfilo.setStateManager(stateManager);

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null)
            controllerMain.updateActiveButtonByState();
    }

    @Override
    public void onExit() {
        // eventuali salvataggi o cleanup, se necessari
    }

    @Override
    public void goBack() {
        stateManager.goBack(); // ritorno allo stato precedente
    }
}