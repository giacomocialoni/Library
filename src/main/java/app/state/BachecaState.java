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
        BachecaControllerGUI controller = stateManager.getStageManager().<BachecaControllerGUI>loadContent(StageManager.BACHECA_VIEW);
        if (controller != null)
            controller.setStateManager(stateManager);

        // aggiorna bottone attivo nella main view
        MainControllerGUI controllerMain = stateManager.getStageManager().getMainController();
        controllerMain.updateActiveButtonByState();
    }

    @Override
    public void onExit() {
        // eventuali salvataggi o cleanup
    }

    @Override
    public void goBack() {
        stateManager.setState(new MainState(stateManager));
    }
}