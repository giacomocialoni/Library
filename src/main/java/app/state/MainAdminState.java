package app.state;

public class MainAdminState extends MainState {
    
    public MainAdminState(StateManager stateManager) {
        super(stateManager);
    }
    
    @Override
    protected void loadMainView() {
        stateManager.getStageManager().loadMainAdminView();
    }
    
    @Override
    protected AppState getDefaultPrimaryState() {
        return new ReservationState(stateManager);
    }
}