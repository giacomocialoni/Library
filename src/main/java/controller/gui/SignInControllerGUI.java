package controller.gui;

import app.state.*;
import bean.AccountBean;
import controller.app.SignInController;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SignInControllerGUI {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;
    @FXML private Button backButton;
    @FXML private VBox loginContainer;
    @FXML private Label errorLabel;
    private FadeTransition errorFade;

    private StateManager stateManager;
    private SignInController signInController;

    private boolean passwordFieldTouched = false;
    private boolean repeatPasswordFieldTouched = false;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.signInController = new SignInController();
        if (backButton != null) updateBackButtonText();
    }

    @FXML
    private void initialize() {
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                passwordFieldTouched = true;
                validatePasswordLength();
            }
        });

        repeatPasswordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                repeatPasswordFieldTouched = true;
                if (passwordFieldTouched) validatePasswordMatch();
                else validatePasswordLength();
            }
        });
    }

    @FXML
    private void handleSignIn() {
        try {
            passwordFieldTouched = true;
            repeatPasswordFieldTouched = true;

            if (!validateAllFields()) return;

            AccountBean accountBean = signInController.signIn(
                    emailField.getText().trim(),
                    passwordField.getText(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim()
            );

            if (accountBean != null) {
                // Navigazione post-registrazione
                stateManager.setState(new MainUserState(stateManager));
            }

        } catch (IllegalArgumentException e) {
            showError("Tutti i campi sono obbligatori!");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ================== VALIDAZIONE ==================
    private boolean validateAllFields() {
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() || passwordField.getText().isEmpty()) {
            showError("Tutti i campi sono obbligatori!");
            return false;
        }

        if (!isValidEmail(emailField.getText())) {
            showError("Inserisci un'email valida!");
            shakeNode(emailField);
            return false;
        }

        if (!validatePasswordLength()) return false;

        if (passwordFieldTouched && repeatPasswordFieldTouched && !validatePasswordMatch()) return false;

        return true;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean validatePasswordLength() {
        if (passwordField.getText().length() < 8) {
            showError("La password deve essere di almeno 8 caratteri!");
            shakeNode(passwordField);
            return false;
        }
        return true;
    }

    private boolean validatePasswordMatch() {
        if (!passwordField.getText().equals(repeatPasswordField.getText())) {
            showError("Le password non coincidono!");
            shakeNode(passwordField);
            shakeNode(repeatPasswordField);
            return false;
        }
        errorLabel.setVisible(false);
        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setOpacity(1.0);
        errorLabel.setVisible(true);

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