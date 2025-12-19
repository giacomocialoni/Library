package controller.gui;

import app.state.StateManager;
import bean.BookBean;
import controller.app.CatalogoController;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
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

        List<BookBean> books = appController.getAllBooks();
        BookCardFactory cardFactory = new BookCardFactory(stateManager);

        for (BookBean book : books) {
            StackPane bookBox = cardFactory.createBookCard(book);
            booksFlowPane.getChildren().add(bookBox);
        }
    }

    @FXML
    private void initialize() {
        // OK
    }
}