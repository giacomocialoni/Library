package app.state;

import controller.gui.MainControllerGUI;

public class MainUserState implements AppState {

    private final StateManager stateManager;

    public MainUserState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        // Carica la view user se non già caricata
        stateManager.getStageManager().loadMainUserView();

        MainControllerGUI controller = stateManager.getStageManager().getActiveMainController();
        if (controller != null) {
            controller.setStateManager(stateManager);
            // Mostra subito il catalogo
            stateManager.setState(new CatalogoState(stateManager));
        } else {
            System.err.println("MainControllerGUI non inizializzato!");
        }
    }

    @Override
    public void onExit() { }

    @Override
    public void goBack() { }
}