package controller.gui;

import app.state.StateManager;
import controller.app.BorrowController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import model.Book;
import utils.BorrowResult;

public class ConfirmBorrowControllerGUI {

    @FXML private Label titleLabel;
    @FXML private Label bookTitleLabel;
    @FXML private Label durationLabel;
    @FXML private Label priceLabel;
    @FXML private Label availabilityLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private StateManager stateManager;
    private Book book;
    private BorrowController borrowController;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.borrowController = new BorrowController();
    }

    // MODIFICATO: rimuovi il parametro previousState
    public void setBorrowData(Book book) {
        this.book = book;
        updateUI();
    }

    private void updateUI() {
        // Popola l'interfaccia con i dati del prestito
        titleLabel.setText("Conferma Prestito");
        bookTitleLabel.setText(book.getTitle());
        durationLabel.setText("Durata: 30 giorni");
        priceLabel.setText("Gratis");
        availabilityLabel.setText("Disponibili: " + book.getStock() + " copie");
        
        // Disabilita il pulsante se stock insufficiente
        confirmButton.setDisable(book.getStock() <= 0);
        
        if (book.getStock() <= 0) {
            confirmButton.setDisable(true);
            confirmButton.setText("Non disponibile");
        } else {
            confirmButton.setText("Conferma Prestito");
        }
    }

    @FXML
    private void handleCancel() {
        stateManager.goBack();
    }

    @FXML
    private void handleConfirm() {
        BorrowResult result = borrowController.borrowBook(book.getId());
        
        switch (result) {
            case SUCCESS -> {
                showSuccess("Prestito effettuato con successo! Ricorda di restituire entro 30 giorni.");
                stateManager.goBack();
            }
            case INSUFFICIENT_STOCK -> {
                showError("Libro non più disponibile per il prestito!");
                updateUI(); // Ricarica i dati
            }
            case MAX_LOANS_REACHED -> 
                showError("Hai raggiunto il limite massimo di 3 prestiti attivi.");
            case EXPIRED_LOAN_EXISTS -> 
                showError("Hai prestiti scaduti da restituire prima di prenderne di nuovi.");
            case ERROR -> 
                showError("Errore durante il prestito. Riprova.");
            case NOT_LOGGED -> 
                showError("Errore sessione persa. Riprova.");
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prestito Confermato");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore Prestito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}