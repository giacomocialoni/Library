package controller.gui;

import app.state.AppState;
import app.state.CatalogoState;
import app.state.CercaState;
import app.state.LoginState;
import app.state.StateManager;
import controller.app.BookDetailController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import model.Book;
import utils.BuyResult;
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

    private int bookId;
    private StateManager stateManager;
    private BookDetailController appController;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.appController = new BookDetailController();
    }

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1);
        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setEditable(true);
    }

    public void loadBook(int bookId) {
        this.bookId = bookId;
        updateBackButtonText();

        Book book = appController.getBookById(bookId);
        if (book == null) {
            titleLabel.setText("Libro non trovato");
            return;
        }

        // Delego la parte di visualizzazione a una factory separata
        BookDetailFactory.populateBookDetails(this, book);
    }

    @FXML
    public void handleBuy() {
        int quantity = quantitySpinner.getValue();
        BuyResult result = appController.buyBook(bookId, quantity);
        switch (result) {
	        case SUCCESS -> {
	        	// TODO Successo
	        }
	        case NOT_LOGGED -> {
	        	stateManager.setState(new LoginState(stateManager));
	        }
	        case INSUFFICIENT_STOCK -> {
	        	// TODO Gestisci insufficient stock
	        }
	        case ERROR -> {
	        	// TODO errore
	        }
        }
        
    }

    @FXML
    public void handleBorrow() {
    	int quantity = quantitySpinner.getValue();
        BorrowResult result = appController.borrowBook(bookId, quantity);
        switch (result) {
	        case SUCCESS -> {
	        	// TODO Successo
	        }
	        case NOT_LOGGED -> {
	        	stateManager.setState(new LoginState(stateManager));
	        }
	        case INSUFFICIENT_STOCK -> {
	        	// TODO Gestisci insufficient stock
	        }
	        case NOT_AUTORIZED -> {
	        	// TODO Not autorized
	        }
	        case ERROR -> {
	        	// TODO errore
	        }
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

    // Getters per permettere alla factory di accedere ai componenti
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
}