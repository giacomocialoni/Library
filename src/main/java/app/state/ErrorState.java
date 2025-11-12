package app.state;

import app.StageManager;
import controller.gui.ErrorControllerGUI;

public class ErrorState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;
    private final String errorMessage;

    public ErrorState(StateManager stateManager, String errorMessage) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
        this.errorMessage = errorMessage;
    }

    @Override
    public void onEnter() {
        ErrorControllerGUI controller = stageManager.loadContent(StageManager.ERROR_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            controller.setErrorData(errorMessage);
        }
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