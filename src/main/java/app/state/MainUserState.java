package app.state;

public class MainUserState extends MainState {
    
    public MainUserState(StateManager stateManager) {
        super(stateManager);
    }
    
    @Override
    protected void loadMainView() {
        stateManager.getStageManager().loadMainUserView();
    }
    
    @Override
    protected AppState getDefaultPrimaryState() {
        return new CatalogoState(stateManager);
    }
}