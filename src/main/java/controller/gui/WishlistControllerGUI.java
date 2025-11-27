package controller.gui;

import java.util.List;

import app.Session;
import app.state.StateManager;
import controller.app.WishlistController;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.Book;
import model.User;
import view.components.BookCardFactory;
import view.components.WishlistBookCardFactory;

public class WishlistControllerGUI {

    private StateManager stateManager;
    private final WishlistController appController = new WishlistController();
    private User user;

    @FXML
    private VBox mainVBox;

    @FXML
    private FlowPane wishlistFlowPane;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.user = (User) Session.getInstance().getLoggedUser();

        if (user != null) {
            populateWishlist();
        }
    }

    @FXML
    public void populateWishlist() {
        wishlistFlowPane.getChildren().clear();

        List<Book> books = appController.getWishlistBooks(user.getEmail());
        BookCardFactory baseFactory = new BookCardFactory(stateManager);
        WishlistBookCardFactory wishlistCardFactory =
                new WishlistBookCardFactory(baseFactory, appController, user);

        for (Book b : books) {
            wishlistFlowPane.getChildren().add(wishlistCardFactory.createWishlistCard(b));
        }
    }
}