package controller.gui;

import java.util.List;

import app.Session;
import app.state.StateManager;
import controller.app.WishlistController;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import bean.UserBean;
import bean.BookBean;
import view.components.BookCardFactory;
import view.components.WishlistBookCardFactory;

public class WishlistControllerGUI {

    private StateManager stateManager;
    private final WishlistController appController = new WishlistController();
    private UserBean user;

    @FXML
    private VBox mainVBox;

    @FXML
    private FlowPane wishlistFlowPane;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;

        String email = Session.getInstance().getLoggedUser().getEmail();
        this.user = appController.getUser(email);
        if (user != null) {
            populateWishlist();
        }
    }

    @FXML
    public void populateWishlist() {
        wishlistFlowPane.getChildren().clear();

        List<BookBean> bookBeans = appController.getWishlistBooks(user.getEmail());

        BookCardFactory baseFactory = new BookCardFactory(stateManager);
        WishlistBookCardFactory wishlistCardFactory =
                new WishlistBookCardFactory(baseFactory, appController, user.getEmail());

        for (BookBean bookBean : bookBeans) {
            wishlistFlowPane.getChildren().add(wishlistCardFactory.createWishlistCard(bookBean));
        }
    }
}