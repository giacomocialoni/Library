package app.state;

import app.StageManager;
import controller.gui.SignInControllerGUI;
import controller.gui.MainControllerGUI;

public class SignInState implements AppState {

    private final StateManager stateManager;

    public SignInState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        // carica la vista nella main view attiva (guest/user)
        SignInControllerGUI controllerSignIn =
            stateManager.getStageManager().<SignInControllerGUI>loadContent(StageManager.SIGN_IN_VIEW);

        if (controllerSignIn != null)
        	controllerSignIn.setStateManager(stateManager);

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