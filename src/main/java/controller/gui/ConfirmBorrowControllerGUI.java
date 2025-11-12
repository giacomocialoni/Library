package controller.gui;

import app.state.ErrorState;
import app.state.SuccessState;
import app.state.StateManager;
import controller.app.BorrowController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import model.Book;
import utils.BorrowResult;

public class ConfirmBorrowControllerGUI {

    @FXML private Label titleLabel;
    @FXML private Label bookTitleLabel;
    @FXML private Label durationLabel;
    @FXML private Label priceLabel;
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
        durationLabel.setText("30 giorni");
        priceLabel.setText("Gratis");
        
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
                stateManager.setState(new SuccessState(
                        stateManager, 
                        "Prestito effettuato con successo! Ricorda di restituire entro 30 giorni."
                    ));
            }
            case INSUFFICIENT_STOCK -> {
                stateManager.setState(new ErrorState(
                    stateManager, 
                    "Libro non più disponibile per il prestito!"
                ));
            }
            case MAX_LOANS_REACHED -> {
                stateManager.setState(new ErrorState(
                    stateManager, 
                    "Hai raggiunto il limite massimo di 3 prestiti attivi.\nRestituisci un libro per prenderne un altro."
                ));
            }
            case EXPIRED_LOAN_EXISTS -> {
                stateManager.setState(new ErrorState(
                    stateManager, 
                    "Hai prestiti scaduti da restituire prima di prenderne di nuovi."
                ));
            }
            case ERROR -> {
                stateManager.setState(new ErrorState(
                    stateManager, 
                    "Errore durante il prestito. Riprova più tardi."
                ));
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + result);
        }
    }
}