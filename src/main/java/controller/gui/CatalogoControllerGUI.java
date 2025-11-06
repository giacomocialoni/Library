package controller.gui;

import app.state.StateManager;
import controller.app.CatalogoController;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.Book;
import view.components.BookCardFactory;

public class CatalogoControllerGUI {

	@FXML private FlowPane booksFlowPane;
    private StateManager stateManager;
    private final CatalogoController appController = new CatalogoController();

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        loadBooks();
    }

    private void loadBooks() {
        booksFlowPane.getChildren().clear();

        List<Book> books = appController.getAllBooks();
        BookCardFactory cardFactory = new BookCardFactory(stateManager);

        for (Book book : books) {
            VBox bookBox = cardFactory.createBookCard(book);
            booksFlowPane.getChildren().add(bookBox);
        }
    }

    @FXML
    private void initialize() {
        // lascia vuoto o solo layout-related
    }
}