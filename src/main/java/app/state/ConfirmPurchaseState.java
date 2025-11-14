package app.state;

import app.StageManager;
import controller.gui.PurchaseControllerGUI;
import model.Book;

public class ConfirmPurchaseState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;
    private final Book book;
    private final int quantity;

    public ConfirmPurchaseState(StateManager stateManager, Book book, int quantity) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
        this.book = book;
        this.quantity = quantity;
    }

    @Override
    public void onEnter() {
        // Carica la vista di conferma acquisto
        PurchaseControllerGUI controller = stageManager.loadContent(StageManager.CONFIRM_PURCHASE_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            controller.setPurchaseData(book, quantity);
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