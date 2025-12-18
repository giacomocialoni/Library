package view.components;

import bean.BookBean;
import controller.app.WishlistController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class WishlistBookCardFactory {

    private final BookCardFactory bookCardFactory;
    private final WishlistController wishlistController;
    private final String userEmail;

    private static final String IN_WISHLIST_STYLE_CLASS = "in-wishlist";

    public WishlistBookCardFactory(BookCardFactory bookCardFactory,
                                   WishlistController wishlistController,
                                   String userEmail) {
        this.bookCardFactory = bookCardFactory;
        this.wishlistController = wishlistController;
        this.userEmail = userEmail;
    }

    public VBox createWishlistCard(BookBean bookBean) {

        // --- Card principale dal BookCardFactory ---
        var bookCard = bookCardFactory.createBookCard(bookBean);

        // --- Bottone toggle wishlist ---
        Button wishlistButton = new Button();
        wishlistButton.getStyleClass().add("wishlist-button");

        boolean inWishlist = wishlistController
		        .getWishlistBooks(userEmail)
		        .stream()
		        .anyMatch(b -> b.getId() == bookBean.getId());

		updateButton(wishlistButton, inWishlist);

        wishlistButton.setOnAction(evt -> {
            boolean currentlyInWishlist = wishlistController
			        .getWishlistBooks(userEmail)
			        .stream()
			        .anyMatch(b -> b.getId() == bookBean.getId());

			if (currentlyInWishlist) {
			    wishlistController.removeFromWishlist(userEmail, bookBean.getId());
			    updateButton(wishlistButton, false);
			} else {
			    wishlistController.addToWishlist(userEmail, bookBean.getId());
			    updateButton(wishlistButton, true);
			}
        });

        VBox container = new VBox(10, bookCard, wishlistButton);
        container.setAlignment(Pos.CENTER);

        return container;
    }

    private void updateButton(Button button, boolean inWishlist) {
        if (inWishlist) {
            button.setText("Rimuovi");
            if (!button.getStyleClass().contains(IN_WISHLIST_STYLE_CLASS)) {
                button.getStyleClass().add(IN_WISHLIST_STYLE_CLASS);
            }
        } else {
            button.setText("Aggiungi");
            button.getStyleClass().remove(IN_WISHLIST_STYLE_CLASS);
        }
    }
}