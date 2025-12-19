package app.state;

/**
 * Stato per pagine di dettaglio (es: BookDetail).
 * Non mostra il menu ma è una destinazione finale per Secondary.
 */
public abstract class DetailState extends AbstractState {
    
    public DetailState(StateManager stateManager) {
        super(stateManager, StateType.DETAIL);
    }
    
    @Override
    public boolean showsMain() {
        return false; // Detail non mostra il menu
    }
    
    @Override
    public boolean hasBackButton() {
        return true; // Detail ha back button
    }
}