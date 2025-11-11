package controller.gui;

import app.state.AppState;
import app.state.BachecaState;
import app.state.CatalogoState;
import app.state.CercaState;
import app.state.MainUserState;
import app.state.StateManager;
import controller.app.SignInController;
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
    
    // Flag per tracciare quali campi sono stati "toccati"
    private boolean passwordFieldTouched = false;
    private boolean repeatPasswordFieldTouched = false;
    
    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.signInController = new SignInController(stateManager);
        if (backButton != null) {
            updateBackButtonText();
        }
    }

    @FXML
    private void initialize() {
        // Listener per focus lost (al rilascio del campo)
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Quando perde il focus
                passwordFieldTouched = true;
                validatePasswordLength();
            }
        });
        
        repeatPasswordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Quando perde il focus
                repeatPasswordFieldTouched = true;
                if (passwordFieldTouched) {
                    validatePasswordMatch();
                } else {
                    validatePasswordLength();
                }
            }
        });
    }

    @FXML
    private void handleSignIn() {
        try {
            // Reset flag
            passwordFieldTouched = true;
            repeatPasswordFieldTouched = true;
            
            // Validazione finale
            if (!validateAllFields()) {
                return;
            }
            
            // Registrazione
            String email = emailField.getText();
            String password = passwordField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            
            Account account = signInController.signIn(email, password, firstName, lastName);

            if (account != null) {
                // Registrazione e login avvenuti con successo
                stateManager.getStageManager().loadMainUserView();
                stateManager.setState(new MainUserState(stateManager));
            }
            
        } catch (IllegalArgumentException e) {
            showError("Tutti i campi sono obbligatori!");
        } catch (Exception e) {
            System.err.println("Errore durante la registrazione: " + e.getMessage());
            showError(e.getMessage());
        }
    }
    
    private boolean validateAllFields() {
        // Validazione campi obbligatori
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() || passwordField.getText().isEmpty()) {
            showError("Tutti i campi sono obbligatori!");
            return false;
        }
        
        // Validazione email
        if (!isValidEmail(emailField.getText())) {
            showError("Inserisci un'email valida!");
            shakeNode(emailField);
            return false;
        }
        
        // Validazione lunghezza password
        if (!validatePasswordLength()) {
            return false;
        }
        
        // Validazione match password (solo se entrambi i campi sono stati toccati)
        if (passwordFieldTouched && repeatPasswordFieldTouched) {
            if (!validatePasswordMatch()) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Regex base per validazione email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    private boolean validatePasswordLength() {
        String password = passwordField.getText();
        if (password.length() < 8) {
            showError("La password deve essere di almeno 8 caratteri!");
            shakeNode(passwordField);
            return false;
        }
        return true;
    }
    
    private boolean validatePasswordMatch() {
        String pass1 = passwordField.getText();
        String pass2 = repeatPasswordField.getText();

        if (!pass1.equals(pass2)) {
            showError("Le password non coincidono!");
            shakeNode(passwordField);
            shakeNode(repeatPasswordField);
            return false;
        }
        
        // Se tutto ok, nasconde errore
        errorLabel.setVisible(false);
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setOpacity(1.0);
        errorLabel.setVisible(true);

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