package controller.gui;

import app.state.ErrorState;
import app.state.StateManager;
import app.state.SuccessState;
import controller.app.PurchaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import model.Book;
import utils.BuyResult;

public class PurchaseControllerGUI {

    @FXML private Label titleLabel;
    @FXML private Label bookTitleLabel;
    @FXML private Label quantityLabel;
    @FXML private Label priceLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private StateManager stateManager;
    private Book book;
    private int quantity;
    private PurchaseController purchaseController;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.purchaseController = new PurchaseController();
    }

    public void setPurchaseData(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
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
    private void handleCancel() {
        // MODIFICATO: usa goBack() invece di setState()
        stateManager.goBack();
    }

    @FXML
    private void handleConfirm() {
        BuyResult result = purchaseController.buyBook(book.getId(), quantity);
        
        switch (result) {
            case SUCCESS -> {
                stateManager.setState(new SuccessState(
                        stateManager, 
                        "Acquisto effettuato con successo!"
                    ));
            }
            case INSUFFICIENT_STOCK -> {
                // MODIFICATO: senza returnState
                stateManager.setState(new ErrorState(
                    stateManager, 
                    "Stock insufficiente! Sono disponibili solo " + book.getStock() + " copie."
                ));
            }
            case ERROR -> {
                stateManager.setState(new ErrorState(
                    stateManager, 
                    "Errore durante l'acquisto. Riprova più tardi."
                ));
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + result);
        }
    }
}