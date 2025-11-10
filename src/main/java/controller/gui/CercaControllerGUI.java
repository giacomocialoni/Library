package controller.gui;

import app.state.StateManager;
import controller.app.CercaController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import model.Book;
import view.components.BookCardFactory;

import java.util.List;

public class CercaControllerGUI {

    // Costanti per le modalità di ricerca
    private static final String SEARCH_MODE_TITLE = "title";
    private static final String SEARCH_MODE_AUTHOR = "author";
    private static final String ACTIVE_STYLE_CLASS = "active";
    
    @FXML private Button searchByTitleButton;
    @FXML private Button searchByAuthorButton;
    @FXML private Label searchLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> genereCombo;
    @FXML private TextField annoFromField;
    @FXML private TextField annoToField;
    @FXML private CheckBox showUnavailableCheckbox;
    @FXML private FlowPane resultsFlowPane;
    @FXML private Label resultsLabel;

    private String searchMode = SEARCH_MODE_TITLE;
    private StateManager stateManager;
    private final CercaController appController = new CercaController();

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @FXML
    public void initialize() {
        genereCombo.getItems().clear();
        genereCombo.getItems().add(null);

        try {
            List<String> nomiCategorie = appController.getAllCategoryNames();
            genereCombo.getItems().addAll(nomiCategorie);
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectSearchByTitle();
    }

    @FXML
    public void selectSearchByTitle() {
        searchMode = SEARCH_MODE_TITLE;
        updateSearchSelector();
        searchLabel.setText("Titolo del libro");
        searchField.setPromptText("Inserisci il titolo del libro...");
    }

    @FXML
    public void selectSearchByAuthor() {
        searchMode = SEARCH_MODE_AUTHOR;
        updateSearchSelector();
        searchLabel.setText("Autore del libro");
        searchField.setPromptText("Inserisci il nome dell'autore...");
    }

    private void updateSearchSelector() {
        searchByTitleButton.getStyleClass().remove(ACTIVE_STYLE_CLASS);
        searchByAuthorButton.getStyleClass().remove(ACTIVE_STYLE_CLASS);

        if (SEARCH_MODE_TITLE.equals(searchMode))
            searchByTitleButton.getStyleClass().add(ACTIVE_STYLE_CLASS);
        else
            searchByAuthorButton.getStyleClass().add(ACTIVE_STYLE_CLASS);
    }

    @FXML
    public void handleSearch() {
        resultsFlowPane.getChildren().clear();

        String searchText = searchField.getText().toLowerCase();
        String category = genereCombo.getValue();
        String yearFrom = annoFromField.getText();
        String yearTo = annoToField.getText();
        boolean includeUnavailable = showUnavailableCheckbox.isSelected();

        List<Book> books = appController.searchBooks(searchText, searchMode, category, yearFrom, yearTo, includeUnavailable);

        BookCardFactory cardFactory = new BookCardFactory(stateManager);

        for (Book book : books) {
            StackPane bookBox = cardFactory.createBookCard(book);
            resultsFlowPane.getChildren().add(bookBox);
        }

        resultsLabel.setText("Risultati: " + books.size());
    }

    @FXML
    public void handleClearFilters() {
        searchField.clear();
        genereCombo.setValue(null);
        annoFromField.clear();
        annoToField.clear();
        showUnavailableCheckbox.setSelected(false);
        resultsFlowPane.getChildren().clear();
        resultsLabel.setText("Risultati della Ricerca");
    }
}