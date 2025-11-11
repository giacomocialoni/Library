package controller.gui;

import app.state.AppState;
import app.state.StateManager;
import controller.app.PurchaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import model.Book;
import utils.BuyResult;

public class ConfirmPurchaseControllerGUI {

    @FXML private Label titleLabel;
    @FXML private Label bookTitleLabel;
    @FXML private Label quantityLabel;
    @FXML private Label priceLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private StateManager stateManager;
    private Book book;
    private int quantity;
    private AppState previousState;
    private PurchaseController purchaseController;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.purchaseController = new PurchaseController();
    }

    public void setPurchaseData(Book book, int quantity, AppState previousState) {
        this.book = book;
        this.quantity = quantity;
        this.previousState = previousState;
        updateUI();
    }

    private void updateUI() {
        // Popola l'interfaccia con i dati dell'acquisto
        titleLabel.setText("Conferma Acquisto");
        bookTitleLabel.setText(book.getTitle());
        quantityLabel.setText(quantity + " copie");
        
        // Prezzo unitario
        priceLabel.setText(String.format("%.2f €", book.getPrice()));
        
        // Calcola il totale
        double total = book.getPrice() * quantity;
        
        // Disabilita il pulsante se stock insufficiente
        confirmButton.setDisable(book.getStock() < quantity);
        
        if (book.getStock() < quantity) {
            confirmButton.setDisable(true);
            confirmButton.setText("Stock insufficiente");
        } else {
            confirmButton.setText("Conferma Acquisto - €" + String.format("%.2f", total));
        }
    }

    @FXML
    private void handleConfirm() {
        BuyResult result = purchaseController.buyBook(book.getId(), quantity);
        
        switch (result) {
            case SUCCESS -> {
                showSuccess("Acquisto effettuato con successo!");
                stateManager.setState(previousState);
            }
            case INSUFFICIENT_STOCK -> {
                showError("Stock insufficiente! Aggiornando i dati...");
                updateUI(); // Ricarica i dati
            }
            case ERROR -> showError("Errore durante l'acquisto. Riprova.");
            case NOT_LOGGED -> showError("Errore sessione persa. Riprova.");
        }
    }

    @FXML
    private void handleCancel() {
        stateManager.setState(previousState);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}