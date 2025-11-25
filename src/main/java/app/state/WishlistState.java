package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.WishlistControllerGUI;

public class WishlistState implements AppState {
    private final StateManager stateManager;

    public WishlistState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

	@Override
	public void onEnter() {
        // carica la vista del profilo nella main view attiva
		WishlistControllerGUI controllerWishlist =
            stateManager.getStageManager().<WishlistControllerGUI>loadContent(StageManager.WISHLIST_VIEW);

        if (controllerWishlist != null)
        	controllerWishlist.setStateManager(stateManager);

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null)
            controllerMain.updateActiveButtonByState();
	}

	@Override
	public void onExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goBack() {
		// TODO Auto-generated method stub
		
	}
}