package app.state;

public class MainGuestState extends MainState {
    
    public MainGuestState(StateManager stateManager) {
        super(stateManager);
    }
    
    @Override
    protected void loadMainView() {
        stateManager.getStageManager().loadMainGuestView();
    }
    
    @Override
    protected AppState getDefaultPrimaryState() {
        return new CatalogoState(stateManager);
    }
}