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
        // Carica la view di login
        LoginControllerGUI controller = stateManager.getStageManager().loadContent(StageManager.LOGIN_VIEW);
        controller.setStateManager(stateManager);

        // aggiorna bottone attivo nella main view
        MainControllerGUI controllerMain = stateManager.getStageManager().getMainController();
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