package app.state;

import app.StageManager;
import controller.gui.LoginControllerGUI;

/**
 * Stato di autenticazione per il login.
 * Estende AuthState e carica LoginView.fxml.
 * Supporta callback per azioni dopo login riuscito.
 */
public class LoginState extends AuthState {
    private final Runnable onLoginSuccess;
    
    // Costruttore per backward compatibility
    public LoginState(StateManager stateManager) {
        this(stateManager, null);
    }
    
    // Costruttore con callback
    public LoginState(StateManager stateManager, Runnable onLoginSuccess) {
        super(stateManager);
        this.onLoginSuccess = onLoginSuccess;
    }
    
    @Override
    public void onEnter() {
        LoginControllerGUI controller = stateManager.getStageManager()
            .<LoginControllerGUI>loadContent(StageManager.LOGIN_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            if (onLoginSuccess != null) {
                controller.setOnLoginSuccessCallback(onLoginSuccess);
            }
        }
    }
}