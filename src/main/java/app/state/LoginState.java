package app.state;

import app.StageManager;
import controller.gui.LoginControllerGUI;
import controller.gui.MainControllerGUI;

public class LoginState implements AppState {

    private final StateManager stateManager;

    public LoginState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        // carica la vista nella main view attiva (guest/user)
        LoginControllerGUI controllerLogin =
            stateManager.getStageManager().<LoginControllerGUI>loadContent(StageManager.LOGIN_VIEW);

        if (controllerLogin != null)
        	controllerLogin.setStateManager(stateManager);

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null)
            controllerMain.updateActiveButtonByState();
    }

    @Override
    public void onExit() {
        // Nessuna operazione particolare (potresti aggiungere cleanup in futuro)
    }

    @Override
    public void goBack() {
        // Eventuale torna indietro
    }
}