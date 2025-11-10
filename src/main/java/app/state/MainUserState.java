package app.state;

public class MainUserState extends AbstractMainState {

    public MainUserState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void onEnter() {
        initializeMainState(() -> stateManager.getStageManager().loadMainUserView());
    }
}