package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.ReservationControllerGUI;

public class ReservationState implements AppState {

    private final StateManager stateManager;
    
    public ReservationState(StateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    @Override
    public void onEnter() {
        try {
            ReservationControllerGUI controllerReservation =
                    stateManager.getStageManager().<ReservationControllerGUI>loadContent(StageManager.RESERVATION_VIEW);

            if (controllerReservation != null) {
                controllerReservation.setStateManager(stateManager);
            } else {
                System.err.println("ReservationControllerGUI è null - impossibile caricare la vista");
                return;
            }

            // aggiorna bottone attivo nella main view attiva
            MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
            if (controllerMain != null) {
                controllerMain.updateActiveButtonByState();
            }
        } catch (Exception e) {
            System.err.println("Errore critico in ReservationState.onEnter: " + e.getMessage());
            e.printStackTrace();
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