package app.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.ReturnLoanControllerGUI;

public class ReturnLoanState implements AppState {

	private static final Logger logger = LoggerFactory.getLogger(ReturnLoanState.class);

    private final StateManager stateManager;

    public ReturnLoanState(StateManager stateManager) {
        this.stateManager = stateManager;
    }
    
	@Override
	public void onEnter() {
		ReturnLoanControllerGUI controllerReturnLoan =
                stateManager.getStageManager().<ReturnLoanControllerGUI>loadContent(StageManager.RETURN_LOANS_VIEW);

        if (controllerReturnLoan != null) {
            try {
            	controllerReturnLoan.setStateManager(stateManager);
            } catch (Exception e) {
                logger.error("Errore durante l'inizializzazione di ReservationControllerGUI", e);
            }
        } else {
            logger.warn("ReservationControllerGUI è null - impossibile caricare la vista");
        }

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null) {
            controllerMain.updateActiveButtonByState();
        }
	}

	@Override
	public void onExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goBack() {
        stateManager.goBack();
	}

}
