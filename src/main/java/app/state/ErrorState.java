package app.state;

import app.StageManager;
import controller.gui.ErrorControllerGUI;

public class ErrorState extends SecondaryState {

    private final String errorMessage;

    public ErrorState(StateManager stateManager, String errorMessage) {
        super(stateManager);
        this.errorMessage = errorMessage;
    }

    @Override
    public void onEnter() {
        ErrorControllerGUI controller = stateManager.getStageManager()
            .<ErrorControllerGUI>loadContent(StageManager.ERROR_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            controller.setErrorData(errorMessage);
        }
    }
}