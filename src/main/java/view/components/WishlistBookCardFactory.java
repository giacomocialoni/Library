package view.components;

import controller.app.WishlistController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.Book;
import model.User;

public class WishlistBookCardFactory {

    private final BookCardFactory bookCardFactory;
    private final WishlistController wishlistController;
    private final User user;

    private static final String IN_WISHLIST_STYLE_CLASS = "in-wishlist";

    public WishlistBookCardFactory(BookCardFactory bookCardFactory,
                                   WishlistController wishlistController,
                                   User user) {
        this.bookCardFactory = bookCardFactory;
        this.wishlistController = wishlistController;
        this.user = user;
    }

    public VBox createWishlistCard(Book book) {
        // --- Card principale dal BookCardFactory ---
        VBox cardBox = new VBox();
        cardBox.setAlignment(Pos.CENTER);

        // La card vera e propria
        var bookCard = bookCardFactory.createBookCard(book);

        // --- Bottone toggle wishlist ---
        Button wishlistButton = new Button();
        wishlistButton.getStyleClass().add("wishlist-button");

        // Stato iniziale
        boolean inWishlist = wishlistController.getWishlistBooks(user.getEmail())
                .stream().anyMatch(b -> b.getId() == book.getId());
        updateButton(wishlistButton, inWishlist);

        // Toggle cliccando
        wishlistButton.setOnAction(evt -> {
            try {
                boolean currentlyInWishlist = wishlistController.getWishlistBooks(user.getEmail())
                        .stream().anyMatch(b -> b.getId() == book.getId());

                if (currentlyInWishlist) {
                    wishlistController.removeFromWishlist(user.getEmail(), book.getId());
                    updateButton(wishlistButton, false);
                } else {
                    wishlistController.addToWishlist(user.getEmail(), book.getId());
                    updateButton(wishlistButton, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // --- Contenitore finale ---
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