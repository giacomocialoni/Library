package app.state;

public interface StateChangeListener {
    void onStateChanged(AppState newState);
}