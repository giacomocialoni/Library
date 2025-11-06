package app;

import javafx.application.Application;
import javafx.stage.Stage;
import app.state.CatalogoState;
import app.state.StateManager;
import dao.factory.DAOFactory;

public class ApplicationGUI extends Application {

    private static DAOFactory daoFactory; // viene impostata dal Main

    public static void setDaoFactory(DAOFactory factory) {
        daoFactory = factory;
    }

    @Override
    public void start(Stage primaryStage) {
        if (daoFactory == null) {
            throw new IllegalStateException("DAOFactory non impostata. Chiama setDaoFactory() prima di launchApp().");
        }

        StageManager stageManager = new StageManager(primaryStage, daoFactory);
        StateManager stateManager = stageManager.getStateManager();

        stateManager.setState(new CatalogoState(stateManager));
        }

    public static void launchApp(String[] args) {
        launch(args);
    }
}