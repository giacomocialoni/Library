package app.state;

import java.util.Stack;
import app.StageManager;
import dao.factory.DAOFactory;

public class StateManager {

    private final DAOFactory daoFactory;
    private StageManager stageManager;
    private AppState currentState;
    private final Stack<AppState> history = new Stack<>();

    public StateManager(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    // Gestione dello StageManager
    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    public StageManager getStageManager() {
        return stageManager;
    }

    // Accesso DAO
    public DAOFactory getDaoFactory() {
        return daoFactory;
    }

    // Gestione stati
    public void setState(AppState newState) {
        if (currentState != null) {
            currentState.onExit();
            history.push(currentState);
        }
        currentState = newState;
        currentState.onEnter();
    }

    public AppState getCurrentState() {
        return currentState;
    }

    public void goBack() {
        if (!history.isEmpty()) {
            currentState.onExit();
            currentState = history.pop();
            currentState.onEnter();
        }
    }

    public AppState getPreviousState() {
        return history.isEmpty() ? null : history.peek();
    }
}