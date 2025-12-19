package app.state;

import app.StageManager;
import controller.gui.SignInControllerGUI;

public class SignInState extends AuthState {
    
    public SignInState(StateManager stateManager) {
        super(stateManager);
    }
    
    @Override
    public void onEnter() {
        SignInControllerGUI controller = stateManager.getStageManager()
            .<SignInControllerGUI>loadContent(StageManager.SIGN_IN_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
        }
    }
}