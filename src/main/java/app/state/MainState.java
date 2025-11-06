package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;

public class MainState implements AppState {

    private final StateManager stateManager;

    public MainState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        StageManager stageManager = stateManager.getStageManager();
        MainControllerGUI controller = stageManager.getMainController(); // controller deve essere già inizializzato
        if (controller != null) {
            controller.setStateManager(stateManager);
            // Mostra subito il catalogo
            stateManager.setState(new CatalogoState(stateManager));
        } else {
            System.err.println("MainControllerGUI non inizializzato!");
        }
    }

    @Override
    public void onExit() {
        // Nessuna azione specifica
    }

    @Override
    public void goBack() {
        // Niente, è lo stato base
    }
}