package controller.gui;

import app.Session;
import app.state.AppState;
import app.state.BachecaState;
import app.state.CatalogoState;
import app.state.CercaState;
import app.state.MainUserState;
import app.state.SignInState;
import app.state.StateManager;
import controller.app.LoginController;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Account;

public class LoginControllerGUI {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;
    @FXML private VBox loginContainer;
    @FXML private Label errorLabel;
    private FadeTransition errorFade;

    private StateManager stateManager;
    private LoginController loginController;
    
    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.loginController = new LoginController(stateManager);
        // Aggiorna il testo del pulsante dopo che FXML ha caricato i nodi
        if (backButton != null) {
            updateBackButtonText();
        }
    }

    @FXML
    private void handleLogin() {
        try {
            String email = usernameField.getText();
            String password = passwordField.getText();

            Account account = loginController.login(email, password);

            if (account != null) {
                // Salva l'utente loggato nella sessione
                Session.getInstance().login(account);

                if (account.isAdmin()) {
                    // TODO: MainAdminState
                } else {
                	stateManager.getStageManager().loadMainUserView();

                    // Imposta stato iniziale come MainUserState
                    stateManager.setState(new MainUserState(stateManager));
                }
            } else {
                showError("Credenziali errate!");
                usernameField.setText("");
                passwordField.setText("");
            }

        } catch (IllegalArgumentException e) {
            showError("Campi mancanti!");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Errore durante il login.");
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setOpacity(1.0);
        errorLabel.setVisible(true);

        shakeNode(usernameField);
        shakeNode(passwordField);

        if (errorFade != null) {
            errorFade.stop();
        }

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
        stateManager.goBack();
    }

    private void updateBackButtonText() {
        AppState previous = stateManager.getPreviousState();
        if (previous instanceof CatalogoState)
            backButton.setText("← Torna al Catalogo");
        else if (previous instanceof CercaState)
            backButton.setText("← Torna a Cerca");
        else if (previous instanceof BachecaState)
            backButton.setText("← Torna a Bacheca");
        else
            backButton.setText("← Torna indietro");
    }
}