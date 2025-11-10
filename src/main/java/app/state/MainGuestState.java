package app.state;

import controller.gui.MainControllerGUI;

public class MainGuestState implements AppState {

    private final StateManager stateManager;

    public MainGuestState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
    	stateManager.getStageManager().showGuestView();
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