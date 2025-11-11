package app.state;

import app.StageManager;
import controller.gui.ConfirmBorrowControllerGUI;
import model.Book;

public class ConfirmBorrowState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;
    private final Book book;

    // MODIFICATO: rimuovi previousState dal costruttore
    public ConfirmBorrowState(StateManager stateManager, Book book) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
        this.book = book;
    }

    @Override
    public void onEnter() {
        // Carica la vista di conferma prestito
        ConfirmBorrowControllerGUI controller = stageManager.loadContent(StageManager.CONFIRM_BORROW_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            controller.setBorrowData(book); // MODIFICATO: passa solo il libro
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