package view.components;

import controller.gui.BookDetailControllerGUI;
import javafx.scene.image.Image;
import model.Book;

import java.io.InputStream;

public class BookDetailFactory {

    public static void populateBookDetails(BookDetailControllerGUI controller, Book book) {
        controller.getTitleLabel().setText(book.getTitle());
        controller.getAuthorLabel().setText("di " + book.getAuthor());
        controller.getGenreLabel().setText(book.getCategory());
        controller.getYearLabel().setText(String.valueOf(book.getYear()));
        controller.getPublisherLabel().setText(book.getPublisher());
        controller.getPagesLabel().setText(String.valueOf(book.getPages()));
        controller.getIsbnLabel().setText(book.getIsbn());
        controller.getPlotLabel().setText(book.getPlot());

        String imagePath = "/images/" + book.getImagePath();
        InputStream imageStream = BookDetailFactory.class.getResourceAsStream(imagePath);
        if (imageStream == null) {
            imageStream = BookDetailFactory.class.getResourceAsStream("/images/default.jpg");
        }
        controller.getCoverImage().setImage(new Image(imageStream));

        if (book.getStock() > 0) {
            controller.getAvailabilityLabel().setText("Disponibile (" + book.getStock() + " copie)");
            controller.getAvailabilityLabel().setStyle("-fx-text-fill: #4a7c59;");
            controller.getQuantitySpinner().setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(1, book.getStock(), 1)
            );
        } else {
            controller.getAvailabilityLabel().setText("Non disponibile");
            controller.getAvailabilityLabel().setStyle("-fx-text-fill: #a94442;");
        }
    }
}