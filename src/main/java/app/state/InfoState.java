package app.state;

import app.StageManager;
import controller.gui.InfoControllerGUI;

public class InfoState extends PrimaryState {
    
    public InfoState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    protected void loadContent() {
        InfoControllerGUI controllerInfo =
            stateManager.getStageManager().<InfoControllerGUI>loadContent(StageManager.INFO_VIEW);

        if (controllerInfo != null) {
            controllerInfo.setStateManager(stateManager); // AGGIUNTO: stateManager come parametro
        }
    }
}