package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.PostControllerGUI;

public class PostState implements AppState {

    private final StateManager stateManager;
    
    public PostState(StateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    @Override
    public void onEnter() {
        PostControllerGUI postController =
                stateManager.getStageManager().<PostControllerGUI>loadContent(StageManager.POST_VIEW);

        if (postController != null) {
            postController.setStateManager(stateManager);
            postController.loadPosts(); // Carica i post all'entrata
        }

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null)
            controllerMain.updateActiveButtonByState();
    }

    @Override
    public void onExit() {
        // Cleanup se necessario
    }

    @Override
    public void goBack() {
        stateManager.goBack();
    }
}