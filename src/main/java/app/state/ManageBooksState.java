package app.state;

import app.StageManager;
import controller.gui.ManageBooksControllerGUI;

public class ManageBooksState extends PrimaryState {
    
    public ManageBooksState(StateManager stateManager) {
        super(stateManager);
    }
    
    @Override
    protected void loadContent() {
        ManageBooksControllerGUI manageBookController =
                stateManager.getStageManager().<ManageBooksControllerGUI>loadContent(StageManager.MANAGE_BOOKS_VIEW);

        if (manageBookController != null) {
            manageBookController.setStateManager(stateManager);
            manageBookController.loadBooks();
        }
    }
}