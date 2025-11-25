package controller.gui;

import app.Session;
import app.state.StateManager;
import controller.app.WishlistController;
import model.User;

public class WishlistControllerGUI {

    private StateManager stateManager;
    private final WishlistController appController = new WishlistController();
    private User user;
    
	public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.user = (User) Session.getInstance().getLoggedUser();
        if (user == null) return;

        populateWishlist();
    }

	private void populateWishlist() {
		// TODO Auto-generated method stub
		
	}
}
