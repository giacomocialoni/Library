package app.state;

/**
 * Classe base astratta per tutti gli stati concreti.
 * Fornisce implementazioni di default e struttura comune.
 */
public abstract class AbstractState implements AppState {
    protected final StateManager stateManager;
    protected final StateType type;
    
    protected AbstractState(StateManager stateManager, StateType type) {
        this.stateManager = stateManager;
        this.type = type;
    }
    
    @Override
    public StateType getType() {
        return type;
    }
    
    @Override
    public void goBack() {
        stateManager.goBack();
    }
}