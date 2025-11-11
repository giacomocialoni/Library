package controller.gui;

import app.state.AppState;
import app.state.CatalogoState;
import app.state.CercaState;
import app.state.ConfirmPurchaseState;
import app.state.LoginState;
import app.state.StateManager;
import controller.app.BookDetailController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import model.Book;
import utils.BorrowResult;
import view.components.BookDetailFactory;

public class BookDetailControllerGUI {

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label genreLabel;
    @FXML private Label yearLabel;
    @FXML private Label publisherLabel;
    @FXML private Label pagesLabel;
    @FXML private Label isbnLabel;
    @FXML private Label availabilityLabel;
    @FXML private Label plotLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private ImageView coverImage;
    @FXML private Button backButton;
    @FXML private Button buyButton;
    @FXML private Button borrowButton;

    private int bookId;
    private StateManager stateManager;
    private BookDetailController appController;
    private Book currentBook;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.appController = new BookDetailController();
    }

    public void loadBook(int bookId) {
        this.bookId = bookId;
        updateBackButtonText();

        Book book = appController.getBookById(bookId);
        this.currentBook = book;
        
        if (book == null) {
            titleLabel.setText("Libro non trovato");
            buyButton.setDisable(true);
            borrowButton.setDisable(true);
            return;
        }

        updateQuantitySpinner(book.getStock());
        BookDetailFactory.populateBookDetails(this, book);
        
        double total = book.getPrice() * quantitySpinner.getValue();
        buyButton.setText(String.format("Acquista - €%.2f", total));
    }

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setEditable(true);
        
        quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (currentBook != null) {
                double total = currentBook.getPrice() * newValue;
                buyButton.setText(String.format("Acquista - €%.2f", total));
            }
        });
    }

    private void updateQuantitySpinner(int stock) {
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Math.max(1, stock), 1);
        quantitySpinner.setValueFactory(valueFactory);
    }

    @FXML
    public void handleBuy() {
        int quantity = quantitySpinner.getValue();
        
        if (currentBook != null) {
            stateManager.setState(new ConfirmPurchaseState(
                stateManager, 
                currentBook, 
                quantity, 
                stateManager.getCurrentState() // stato precedente
            ));
        }
    }

    @FXML
    public void handleBorrow() {
        // MODIFICATO: logica temporanea, poi implementerò BorrowStatusState
        BorrowResult result = appController.borrowBook(bookId);
        switch (result) {
            case SUCCESS -> showSuccess("Prestito effettuato con successo!");
            case NOT_LOGGED -> stateManager.setState(new LoginState(stateManager));
            case INSUFFICIENT_STOCK -> showError("Libro non disponibile per il prestito");
            case MAX_LOANS_REACHED -> showError("Hai raggiunto il limite massimo di 3 prestiti attivi");
            case EXPIRED_LOAN_EXISTS -> showError("Hai prestiti scaduti da restituire");
            case ERROR -> showError("Errore durante il prestito");
        }
    }

    @FXML
    public void handleBack() {
        stateManager.goBack();
    }

    private void updateBackButtonText() {
        AppState previous = stateManager.getPreviousState();
        if (previous instanceof CatalogoState)
            backButton.setText("← Torna al Catalogo");
        else if (previous instanceof CercaState)
            backButton.setText("← Torna a Cerca");
        else
            backButton.setText("← Torna indietro");
    }

    // Metodi helper per messaggi (temporanei)
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        loadBook(bookId); // Ricarica per aggiornare disponibilità
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getters
    public Label getTitleLabel() { return titleLabel; }
    public Label getAuthorLabel() { return authorLabel; }
    public Label getGenreLabel() { return genreLabel; }
    public Label getYearLabel() { return yearLabel; }
    public Label getPublisherLabel() { return publisherLabel; }
    public Label getPagesLabel() { return pagesLabel; }
    public Label getIsbnLabel() { return isbnLabel; }
    public Label getAvailabilityLabel() { return availabilityLabel; }
    public Label getPlotLabel() { return plotLabel; }
    public ImageView getCoverImage() { return coverImage; }
    public Spinner<Integer> getQuantitySpinner() { return quantitySpinner; }
    public Button getBuyButton() { return buyButton; }
    public Button getBorrowButton() { return borrowButton; }
}