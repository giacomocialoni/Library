package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import controller.gui.MainGuestControllerGUI;
import controller.gui.MainUserControllerGUI;
import controller.gui.MainControllerGUI;

import dao.factory.DAOFactory;
import app.state.StateManager;

public class StageManager {

    private final StateManager stateManager;
    private final Stage primaryStage;

    private MainGuestControllerGUI mainGuestController;
    private MainUserControllerGUI mainUserController;

    private static final String BASE_PATH = "/view/gui/";
    public static final String MAIN_GUEST_VIEW = BASE_PATH + "MainGuestView.fxml";
    public static final String MAIN_USER_VIEW = BASE_PATH + "MainUserView.fxml";
    public static final String CATALOGO_VIEW = BASE_PATH + "CatalogoView.fxml";
    public static final String CERCA_VIEW = BASE_PATH + "CercaView.fxml";
    public static final String BACHECA_VIEW = BASE_PATH + "BachecaView.fxml";
    public static final String BOOK_DETAIL_VIEW = BASE_PATH + "BookDetailView.fxml";
    public static final String LOGIN_VIEW = BASE_PATH + "LoginView.fxml";
    public static final String SIGN_IN_VIEW = BASE_PATH + "SignInView.fxml";
    public static final String PROFILO_VIEW = BASE_PATH + "ProfiloView.fxml";
    public static final String CONFIRM_PURCHASE_VIEW = BASE_PATH + "ConfirmPurchaseView.fxml";
    public static final String CONFIRM_BORROW_VIEW = BASE_PATH + "ConfirmBorrowView.fxml";

    public StageManager(Stage stage, DAOFactory daoFactory) {
        this.primaryStage = stage;
        this.stateManager = new StateManager(daoFactory);
        this.stateManager.setStageManager(this); // collega lo state manager
        showGuestView(); // mostra la view guest all'avvio
    }

    public void showGuestView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_GUEST_VIEW));
            Parent root = loader.load();

            mainGuestController = loader.getController();
            mainGuestController.setStateManager(stateManager);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Library");
            primaryStage.show();

            // reset controller user
            mainUserController = null;

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della view guest: " + e.getMessage());
        }
    }

    /** Mostra la view User dopo login */
    public MainUserControllerGUI loadMainUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_USER_VIEW));
            Parent root = loader.load();

            MainUserControllerGUI controller = loader.getController();
            controller.setStateManager(stateManager);

            mainUserController = controller;

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Library - User");
            
            // Forza il ridimensionamento corretto
            primaryStage.sizeToScene();
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();

            return controller;
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della view user: " + e.getMessage());
            return null;
        }
    }

    /** Ritorna il controller principale attivo (guest o user) */
    public MainControllerGUI getActiveMainController() {
        if (mainUserController != null)
            return mainUserController;
        return mainGuestController;
    }

    /** Carica un contenuto FXML nella view attiva */
    @SuppressWarnings("unchecked")
    public <T> T loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();

            MainControllerGUI activeController = getActiveMainController();
            if (activeController != null)
                activeController.setContent(node);

            return (T) loader.getController();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del contenuto " + fxmlPath + ": " + e.getMessage());
            return null;
        }
    }

    public StateManager getStateManager() {
        return stateManager;
    }
}