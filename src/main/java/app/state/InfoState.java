package app.state;

import app.StageManager;
import controller.gui.InfoControllerGUI;
import controller.gui.MainControllerGUI;

public class InfoState implements AppState {

    private final StateManager stateManager;
	
	public InfoState(StateManager stateManager) {
		this.stateManager = stateManager;
	}

	@Override
	public void onEnter() {
		InfoControllerGUI controllerInfo =
	            stateManager.getStageManager().<InfoControllerGUI>loadContent(StageManager.INFO_VIEW);

	        if (controllerInfo != null)
	        	controllerInfo.setStateManager();
		
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
