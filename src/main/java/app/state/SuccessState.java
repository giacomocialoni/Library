package app.state;

import app.StageManager;
import controller.gui.SuccessControllerGUI;

public class SuccessState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;
    private final String successMessage;

    public SuccessState(StateManager stateManager, String successMessage) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
        this.successMessage = successMessage;
    }

    @Override
    public void onEnter() {
        SuccessControllerGUI controller = stageManager.loadContent(StageManager.SUCCESS_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            controller.setSuccessData(successMessage);
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