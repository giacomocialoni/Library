package app.state;

import app.StageManager;
import controller.gui.PostControllerGUI;

public class PostState extends PrimaryState {
    
    public PostState(StateManager stateManager) {
        super(stateManager);
    }
    
    @Override
    protected void loadContent() {
        PostControllerGUI postController =
                stateManager.getStageManager().<PostControllerGUI>loadContent(StageManager.POST_VIEW);

        if (postController != null) {
            postController.setStateManager(stateManager);
            postController.loadPosts();
        }
    }
}