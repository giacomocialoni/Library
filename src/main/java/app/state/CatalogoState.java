package app.state;

import app.StageManager;
import controller.gui.CatalogoControllerGUI;
import controller.gui.MainControllerGUI;

public class CatalogoState implements AppState {

    private final StateManager stateManager;

    public CatalogoState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        // carica la vista nella main view attiva (guest/user)
        CatalogoControllerGUI controllerCatalogo =
            stateManager.getStageManager().<CatalogoControllerGUI>loadContent(StageManager.CATALOGO_VIEW);

        if (controllerCatalogo != null)
            controllerCatalogo.setStateManager(stateManager);

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
        stateManager.goBack(); // se serve, anche questo può essere guest/user-aware
    }
}