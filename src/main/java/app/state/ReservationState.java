package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.ReservationControllerGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationState implements AppState {

    private static final Logger logger = LoggerFactory.getLogger(ReservationState.class);

    private final StateManager stateManager;

    public ReservationState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        ReservationControllerGUI controllerReservation =
                stateManager.getStageManager().<ReservationControllerGUI>loadContent(StageManager.RESERVATION_VIEW);

        if (controllerReservation != null) {
            try {
                controllerReservation.setStateManager(stateManager);
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
        // Cleanup se necessario
    }

    @Override
    public void goBack() {
        stateManager.goBack();
    }
}