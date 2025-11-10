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

    private String searchMode = "title";
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
        searchMode = "title";
        updateSearchSelector();
        searchLabel.setText("Titolo del libro");
        searchField.setPromptText("Inserisci il titolo del libro...");
    }

    @FXML
    public void selectSearchByAuthor() {
        searchMode = "author";
        updateSearchSelector();
        searchLabel.setText("Autore del libro");
        searchField.setPromptText("Inserisci il nome dell'autore...");
    }

    private void updateSearchSelector() {
        searchByTitleButton.getStyleClass().remove("active");
        searchByAuthorButton.getStyleClass().remove("active");

        if (searchMode.equals("title"))
            searchByTitleButton.getStyleClass().add("active");
        else
            searchByAuthorButton.getStyleClass().add("active");
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