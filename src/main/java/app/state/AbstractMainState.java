package app.state;

import controller.gui.MainControllerGUI;

public abstract class AbstractMainState implements AppState {
    protected final StateManager stateManager;

    protected AbstractMainState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    protected void initializeMainState(Runnable viewLoader) {
        viewLoader.run();
        
        MainControllerGUI controller = stateManager.getStageManager().getActiveMainController();
        if (controller != null) {
            controller.setStateManager(stateManager);
            if (this instanceof MainUserState || this instanceof MainGuestState) {
                stateManager.setState(new CatalogoState(stateManager));
            } else if (this instanceof MainAdminState) {
            	stateManager.setState(new ReservationState(stateManager));
            }
        } else {
            System.err.println("MainControllerGUI non inizializzato!");
        }
    }

    @Override
    public void onExit() { }

    @Override
    public void goBack() { }
}