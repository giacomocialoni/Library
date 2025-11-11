package app.state;

import app.StageManager;
import controller.gui.LoginControllerGUI;

public class LoginState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;
    private final Runnable onLoginSuccess; // NUOVO: callback per login riuscito

    // Costruttore esistente per backward compatibility
    public LoginState(StateManager stateManager) {
        this(stateManager, null);
    }

    // NUOVO: costruttore con callback
    public LoginState(StateManager stateManager, Runnable onLoginSuccess) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
        this.onLoginSuccess = onLoginSuccess;
    }

    @Override
    public void onEnter() {
        LoginControllerGUI controller = stageManager.loadContent(StageManager.LOGIN_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            // NUOVO: passa il callback al controller
            if (onLoginSuccess != null) {
                controller.setOnLoginSuccessCallback(onLoginSuccess);
            }
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