package app.state;

import app.StageManager;
import controller.gui.CercaControllerGUI;

public class CercaState extends PrimaryState {

    public CercaState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    protected void loadContent() {
        CercaControllerGUI controllerCerca =
            stateManager.getStageManager().<CercaControllerGUI>loadContent(StageManager.CERCA_VIEW);

        if (controllerCerca != null) {
            controllerCerca.setStateManager(stateManager);
        }
    }
}