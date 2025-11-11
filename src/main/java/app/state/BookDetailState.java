package app.state;

import app.StageManager;
import controller.gui.BookDetailControllerGUI;

public class BookDetailState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;
    private final int bookId;

    public BookDetailState(StateManager stateManager, int bookId) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
        this.bookId = bookId;
    }

    @Override
    public void onEnter() {
        // Carica la vista dei dettagli libro nel content principale
        BookDetailControllerGUI controller = stageManager.<BookDetailControllerGUI>loadContent(StageManager.BOOK_DETAIL_VIEW);
        if (controller != null) {
            controller.setStateManager(stateManager);
            controller.loadBook(bookId);
        }
    }

    @Override
    public void onExit() {
        // eventuali cleanup
    }

    @Override
    public void goBack() {
        // MODIFICATO: usa goBack() per tornare allo stato precedente
        stateManager.goBack();
    }
}