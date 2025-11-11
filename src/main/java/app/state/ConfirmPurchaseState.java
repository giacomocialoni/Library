package app.state;

import app.StageManager;
import controller.gui.ConfirmPurchaseControllerGUI;
import model.Book;

public class ConfirmPurchaseState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;
    private final Book book;
    private final int quantity;
    private final AppState previousState;

    public ConfirmPurchaseState(StateManager stateManager, Book book, int quantity, AppState previousState) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
        this.book = book;
        this.quantity = quantity;
        this.previousState = previousState;
    }

    @Override
    public void onEnter() {
        // Carica la vista di conferma acquisto
        ConfirmPurchaseControllerGUI controller = stageManager.loadContent(StageManager.CONFIRM_PURCHASE_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            controller.setPurchaseData(book, quantity, previousState);
        }
    }

    @Override
    public void onExit() {
        // Cleanup se necessario
    }

    @Override
    public void goBack() {
        // Torna allo stato precedente
        stateManager.setState(previousState);
    }
}