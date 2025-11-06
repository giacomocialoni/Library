package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import controller.gui.MainControllerGUI;
import dao.factory.DAOFactory;
import app.state.StateManager;

public class StageManager {
    private final StateManager stateManager;    
    private final Stage primaryStage;
    private MainControllerGUI mainController;

    private static final String BASE_PATH = "/view/gui/";
    public static final String MAIN_VIEW = BASE_PATH + "MainView.fxml";
    public static final String CATALOGO_VIEW = BASE_PATH + "CatalogoView.fxml";
    public static final String CERCA_VIEW = BASE_PATH + "CercaView.fxml";
    public static final String BACHECA_VIEW = BASE_PATH + "BachecaView.fxml";
    public static final String BOOK_DETAIL_VIEW = BASE_PATH + "BookDetailView.fxml";
    public static final String LOGIN_VIEW = BASE_PATH + "LoginView.fxml";

    public StageManager(Stage stage, DAOFactory daoFactory) {
        this.primaryStage = stage;
        this.stateManager = new StateManager(daoFactory);
        this.stateManager.setStageManager(this); // collega lo state manager
        initMainView();
    }

    private void initMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW));
            Parent root = loader.load();

            // Recupero controller principale
            mainController = loader.getController();
            mainController.setStateManager(stateManager); // collega lo state manager

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Library");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainControllerGUI getMainController() {
        return mainController;
    }

    @SuppressWarnings("unchecked")
    public <T> T loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();
            mainController.setContent(node);
            return (T) loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

	public StateManager getStateManager() {return stateManager;}
}