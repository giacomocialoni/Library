package controller.gui;

import app.state.StateManager;
import app.state.ErrorState;
import app.state.SuccessState;
import controller.app.ManageBooksController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Book;
import view.components.ManageBooksCardFactory;

import java.util.List;

public class ManageBooksControllerGUI {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private Button addBookButton;
    @FXML private VBox resultsContainer;
    @FXML private Label resultsLabel;

    private StateManager stateManager;
    private final ManageBooksController appController = new ManageBooksController();
    private ManageBooksCardFactory cardFactory;
    
    private boolean initialized = false;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.cardFactory = new ManageBooksCardFactory();
        
        if (initialized) {
            loadBooks();
        }
    }

    @FXML
    public void initialize() {
        initialized = true;
    }

    @FXML
    public void handleSearch() {
        if (cardFactory == null) {
            showError("Attenzione", "Sistema non ancora inizializzato");
            return;
        }

        String searchText = searchField.getText().trim();
        List<Book> books;

        if (searchText.isEmpty()) {
            books = appController.getAllBooks();
        } else {
            books = appController.searchBooks(searchText);
        }

        displayBooks(books);
    }

    @FXML
    public void handleClearFilters() {
        searchField.clear();
        loadBooks();
    }

    @FXML
    public void addBook() {
        // TODO: Implementare navigazione alla view di aggiunta libro
        showSuccess("Info", "Funzionalità di aggiunta libro da implementare");
    }

    public void loadBooks() {
        if (cardFactory == null) return;

        List<Book> books = appController.getAllBooks();
        displayBooks(books);
    }

    private void displayBooks(List<Book> books) {
        resultsContainer.getChildren().clear();

        for (Book book : books) {
            var bookCard = cardFactory.createBookCard(
                book,
                (quantity) -> handleIncreaseStock(book.getId(), quantity),
                (quantity) -> handleDecreaseStock(book.getId(), quantity),
                () -> handleRemoveBook(book.getId())
            );
            resultsContainer.getChildren().add(bookCard);
        }

        updateResultsLabel(books.size());
    }

    private void updateResultsLabel(int count) {
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
            resultsLabel.setText("Totale libri: " + count);
        } else {
            resultsLabel.setText("Trovati " + count + " libri per '" + searchText + "'");
        }
    }

    // ===== GESTIONE AZIONI =====

    private void handleIncreaseStock(int bookId, int quantity) {
        try {
            appController.increaseStock(bookId, quantity);
            loadBooks(); // Ricarica la lista
            showSuccess("Successo", "Stock aumentato di " + quantity + " unità");
        } catch (Exception e) {
            showError("Errore", "Errore nell'aumentare lo stock: " + e.getMessage());
        }
    }

    private void handleDecreaseStock(int bookId, int quantity) {
        try {
            Book book = appController.getBookById(bookId);
            if (book != null && book.getStock() >= quantity) {
                appController.decreaseStock(bookId, quantity);
                loadBooks(); // Ricarica la lista
                showSuccess("Successo", "Stock diminuito di " + quantity + " unità");
            } else {
                showError("Errore", "Stock insufficiente per diminuire di " + quantity + " unità");
            }
        } catch (Exception e) {
            showError("Errore", "Errore nel diminuire lo stock: " + e.getMessage());
        }
    }

    private void handleRemoveBook(int bookId) {
        try {
            // Conferma prima di eliminare
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma eliminazione");
            alert.setHeaderText("Eliminare il libro?");
            alert.setContentText("Questa operazione non può essere annullata.");
            
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                appController.deleteBook(bookId);
                loadBooks(); // Ricarica la lista
                showSuccess("Successo", "Libro eliminato con successo");
            }
        } catch (Exception e) {
            showError("Errore", "Errore nell'eliminare il libro: " + e.getMessage());
        }
    }

    // ===== GESTIONE STATI =====

    private void showSuccess(String title, String message) {
        SuccessState successState = new SuccessState(stateManager, message);
        stateManager.setState(successState);
    }

    private void showError(String title, String message) {
        ErrorState errorState = new ErrorState(stateManager, message);
        stateManager.setState(errorState);
    }
}