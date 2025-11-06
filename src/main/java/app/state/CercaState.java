package app.state;

import app.StageManager;
import controller.gui.CercaControllerGUI;
import controller.gui.MainControllerGUI;

public class CercaState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;

    public CercaState(StateManager stateManager) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
    }

    @Override
    public void onEnter() {
        CercaControllerGUI controller = stageManager.<CercaControllerGUI>loadContent(StageManager.CERCA_VIEW);
        if (controller != null)
            controller.setStateManager(stateManager);

        // aggiorna bottone attivo nella main view
        MainControllerGUI controllerMain = stateManager.getStageManager().getMainController();
        controllerMain.updateActiveButtonByState();
    }

    @Override
    public void onExit() {
        // eventuali cleanup se servono
    }

    @Override
    public void goBack() {
        stateManager.setState(new MainState(stateManager));
    }
}