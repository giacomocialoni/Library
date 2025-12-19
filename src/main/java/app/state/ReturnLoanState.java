package app.state;

import app.StageManager;
import controller.gui.ReturnLoanControllerGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnLoanState extends PrimaryState {

    private static final Logger logger = LoggerFactory.getLogger(ReturnLoanState.class);

    public ReturnLoanState(StateManager stateManager) {
        super(stateManager);
    }
    
    @Override
    protected void loadContent() {
        ReturnLoanControllerGUI controllerReturnLoan =
                stateManager.getStageManager().<ReturnLoanControllerGUI>loadContent(StageManager.RETURN_LOANS_VIEW);

        if (controllerReturnLoan != null) {
            try {
                controllerReturnLoan.setStateManager(stateManager);
            } catch (Exception e) {
                logger.error("Errore durante l'inizializzazione di ReturnLoanControllerGUI", e);
            }
        } else {
            logger.warn("ReturnLoanControllerGUI è null - impossibile caricare la vista");
        }
    }
}