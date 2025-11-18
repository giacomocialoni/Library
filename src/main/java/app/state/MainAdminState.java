package app.state;

import app.StageManager;
import controller.gui.MainAdminControllerGUI;

public class MainAdminState extends AbstractMainState {

    public MainAdminState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void onEnter() {
    	initializeMainState(() -> {
            MainAdminControllerGUI controller = stateManager.getStageManager().loadMainAdminView();
            if (controller != null) {
                stateManager.getStageManager().loadContent(StageManager.RESERVATION_VIEW);
            }
        });
    }
}