package app.state;

import controller.gui.MainControllerGUI;

public abstract class AbstractMainState implements AppState {
    protected final StateManager stateManager;

    public AbstractMainState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    protected void initializeMainState(Runnable viewLoader) {
        viewLoader.run();
        
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