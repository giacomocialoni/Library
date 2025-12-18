package app.state;

import app.StageManager;
import controller.gui.LoanControllerGUI;
import bean.BookBean;

public class ConfirmLoanState implements AppState {

    private final StateManager stateManager;
    private final StageManager stageManager;
    private final BookBean book;

    // MODIFICATO: rimuovi previousState dal costruttore
    public ConfirmLoanState(StateManager stateManager, BookBean book) {
        this.stateManager = stateManager;
        this.stageManager = stateManager.getStageManager();
        this.book = book;
    }

    @Override
    public void onEnter() {
        // Carica la vista di conferma prestito
    	LoanControllerGUI controller = stageManager.loadContent(StageManager.CONFIRM_LOAN_VIEW);
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