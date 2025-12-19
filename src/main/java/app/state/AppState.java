package app.state;

public interface AppState {
    
    /** Chiamato quando lo stato diventa attivo */
    void onEnter();
    
    /** Tipo di stato: MAIN, PRIMARY, SECONDARY, AUTH_FLOW */
    StateType getType();
    
    /** 
     * Indica se il main (menu principale) deve essere visibile in questo stato.
     * Default: true solo per MAIN e PRIMARY.
     */
    default boolean showsMain() {
        return getType() == StateType.MAIN || getType() == StateType.PRIMARY;
    }
    
    /**
     * Indica se questo stato ha un pulsante "Go Back".
     * Default: true solo per SECONDARY e AUTH_FLOW.
     */
    default boolean hasBackButton() {
        return getType() == StateType.SECONDARY || getType() == StateType.AUTH_FLOW;
    }
    
    /**
     * Azione da eseguire quando viene premuto il pulsante "Back".
     * Di default delega allo state manager.
     */
    default void goBack() {
        // Implementazione vuota - sarà gestita dallo StateManager
    }
    
    /**
     * Chiamato quando lo stato viene abbandonato.
     */
    default void onExit() {
        // Default: niente
    }
}