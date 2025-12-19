package app.state;

import app.StageManager;
import controller.gui.CatalogoControllerGUI;

public class CatalogoState extends PrimaryState {
    
    public CatalogoState(StateManager stateManager) {
        super(stateManager);
    }
    
    @Override
    protected void loadContent() {
        CatalogoControllerGUI controllerCatalogo =
            stateManager.getStageManager().<CatalogoControllerGUI>loadContent(StageManager.CATALOGO_VIEW);

        if (controllerCatalogo != null) {
            controllerCatalogo.setStateManager(stateManager);
        }
    }
}