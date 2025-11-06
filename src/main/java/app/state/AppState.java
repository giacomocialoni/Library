package app.state;

public interface AppState {
    void onEnter();
    void onExit();
    void goBack();
}