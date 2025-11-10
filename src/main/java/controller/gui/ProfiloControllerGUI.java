package controller.gui;

import app.Session;
import app.state.MainGuestState;
import app.state.StateManager;
import controller.app.ProfiloController;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import model.Book;
import model.Loan;
import model.User;
import view.components.BookCardFactory;
import view.components.LoanBookCardFactory;

import java.util.List;

public class ProfiloControllerGUI {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button showPasswordButton;
    @FXML private HBox recentPurchasesBox;
    @FXML private HBox loansBox; // Cambiato da ScrollPane a HBox
    @FXML private Button logoutButton;
    @FXML private Label noLoansLabel;

    private StateManager stateManager;
    private final ProfiloController appController = new ProfiloController();
    private BookCardFactory cardFactory;
    private boolean passwordVisible = false;
    private User user;

    private Image visibleIcon;
    private Image invisibleIcon;
    private ImageView eyeView;

    @FXML
    private void initialize() {
        // Carica le icone (dal percorso resources)
        visibleIcon = new Image(getClass().getResource("/images/utils/view.png").toExternalForm());
        invisibleIcon = new Image(getClass().getResource("/images/utils/hide.png").toExternalForm());

        // Crea ImageView per il bottone
        eyeView = new ImageView(invisibleIcon);
        eyeView.setFitWidth(18);
        eyeView.setFitHeight(18);
        showPasswordButton.setGraphic(eyeView);
        showPasswordButton.setStyle("-fx-background-color: transparent;");

        showPasswordButton.setOnMouseEntered(e -> showPasswordButton.setCursor(Cursor.HAND));
        showPasswordButton.setOnMouseExited(e -> showPasswordButton.setCursor(Cursor.DEFAULT));
        
        showPasswordButton.setOnAction(e -> togglePasswordVisibility());
    }

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.user = (User) Session.getInstance().getLoggedUser();
        if (user == null) return;

        this.cardFactory = new BookCardFactory(stateManager);

        populateProfile();
        populateRecentPurchases();
        populateLoans();
    }

    private void populateProfile() {
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        updatePasswordField();
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        updatePasswordField();
        if (passwordVisible) {
            eyeView.setImage(visibleIcon);
        } else {
            eyeView.setImage(invisibleIcon);
        }
    }

    private void updatePasswordField() {
        if (passwordVisible) {
            passwordField.setText(user.getPassword());
        } else {
            // Mostra pallini al posto della password
            passwordField.setText("••••••••");
        }
    }

    private void populateRecentPurchases() {
        recentPurchasesBox.getChildren().clear();
        List<Book> purchasedBooks = appController.getPurchasedBooks(user.getEmail());
        for (Book book : purchasedBooks) {
            StackPane card = cardFactory.createBookCard(book);
            recentPurchasesBox.getChildren().add(card);
        }
    }

    private void populateLoans() {
        loansBox.getChildren().clear();
        List<Loan> loans = appController.getLoans(user.getEmail());

        if (loans.isEmpty()) {
            // Mostra messaggio "Nessun prestito"
            noLoansLabel.setVisible(true);
            noLoansLabel.setManaged(true);
            return;
        }

        // Nascondi messaggio "Nessun prestito"
        noLoansLabel.setVisible(false);
        noLoansLabel.setManaged(false);

        LoanBookCardFactory loanCardFactory = new LoanBookCardFactory(cardFactory);

        for (Loan loan : loans) {
            VBox loanCard = loanCardFactory.createLoanCard(loan);
            loansBox.getChildren().add(loanCard);
        }
    }

    @FXML
    private void handleLogout() {
        Session.getInstance().logout();
        stateManager.setState(new MainGuestState(stateManager));
    }
}