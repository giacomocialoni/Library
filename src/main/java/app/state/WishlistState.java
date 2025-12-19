package app.state;

import app.StageManager;
import controller.gui.WishlistControllerGUI;

public class WishlistState extends PrimaryState {
    
    public WishlistState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    protected void loadContent() {
        WishlistControllerGUI controllerWishlist =
            stateManager.getStageManager().<WishlistControllerGUI>loadContent(StageManager.WISHLIST_VIEW);

        if (controllerWishlist != null) {
            controllerWishlist.setStateManager(stateManager);
        }
    }
}