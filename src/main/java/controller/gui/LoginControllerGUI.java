package controller.gui;

import app.state.*;
import bean.AccountBean;
import controller.app.LoginController;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoginControllerGUI {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;
    @FXML private VBox loginContainer;
    @FXML private Label errorLabel;
    private FadeTransition errorFade;

    private StateManager stateManager;
    private LoginController loginController;
    private Runnable onLoginSuccessCallback;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.loginController = new LoginController();
    }

    public void setOnLoginSuccessCallback(Runnable callback) {
        this.onLoginSuccessCallback = callback;
    }

    @FXML
    private void handleLogin() {
        try {
            String email = usernameField.getText();
            String password = passwordField.getText();

            AccountBean accountBean = loginController.login(email, password);

            if (accountBean == null) {
                showError("Credenziali errate!");
                usernameField.clear();
                passwordField.clear();
                return;
            }

            // NAVIGAZIONE
            if (loginController.isAdmin()) {
                stateManager.setState(new MainAdminState(stateManager));
            } else {
                stateManager.setState(new MainUserState(stateManager));
                if (onLoginSuccessCallback != null) onLoginSuccessCallback.run();
            }

        } catch (IllegalArgumentException e) {
            showError("Compila tutti i campi!");
        } catch (Exception e) {
            showError("Errore durante il login");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setOpacity(1.0);
        errorLabel.setVisible(true);
        shakeNode(usernameField);
        shakeNode(passwordField);

        if (errorFade != null) errorFade.stop();

        errorFade = new FadeTransition(Duration.seconds(0.5), errorLabel);
        errorFade.setFromValue(1.0);
        errorFade.setToValue(0.0);
        errorFade.setDelay(Duration.seconds(5));
        errorFade.setOnFinished(e -> errorLabel.setVisible(false));
        errorFade.play();
    }

    private void shakeNode(Node node) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(50), new KeyValue(node.translateXProperty(), -10)),
            new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), 10)),
            new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), -10)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), 10)),
            new KeyFrame(Duration.millis(250), new KeyValue(node.translateXProperty(), 0))
        );
        timeline.play();
    }

    @FXML
    private void handleRegister() {
        stateManager.setState(new SignInState(stateManager));
    }

    @FXML
    private void handleBack() {
        stateManager.goBack(); // Standard back per Auth states
    }
}