package app.state;

public class MainGuestState extends AbstractMainState {

    public MainGuestState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void onEnter() {
        initializeMainState(() -> stateManager.getStageManager().showGuestView());
    }
}