package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import model.Book;
import app.state.BookDetailState;
import app.state.StateManager;

import java.io.InputStream;

public class BookCardFactory {

    private final StateManager stateManager;

    public BookCardFactory(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public VBox createBookCard(Book book) {
        VBox bookBox = new VBox(8);
        bookBox.getStyleClass().add("book-card");
        bookBox.setAlignment(Pos.CENTER);
        bookBox.setPrefSize(200, 300);
        bookBox.setPadding(new Insets(15));
        bookBox.setMaxSize(200, 300);

        // Immagine copertina
        String imagePath = "/images/" + book.getImagePath();
        InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream == null) {
            System.err.println("Immagine non trovata: " + imagePath);
            imageStream = getClass().getResourceAsStream("/images/default.jpg");
        }

        ImageView cover = new ImageView(new Image(imageStream));
        cover.setFitWidth(150);
        cover.setFitHeight(180);
        cover.setPreserveRatio(true);
        cover.getStyleClass().add("book-cover");

        // Titolo
        Label titleLabel = new Label(book.getTitle());
        titleLabel.getStyleClass().add("book-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(180);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        // Autore
        Label authorLabel = new Label(book.getAuthor());
        authorLabel.getStyleClass().add("book-author");
        authorLabel.setWrapText(true);
        authorLabel.setMaxWidth(180);
        authorLabel.setTextAlignment(TextAlignment.CENTER);

        // Categoria
        Label genreLabel = new Label(book.getCategory());
        genreLabel.getStyleClass().add("book-genre");
        genreLabel.setWrapText(true);
        genreLabel.setMaxWidth(180);
        genreLabel.setAlignment(Pos.CENTER);

        bookBox.getChildren().addAll(cover, titleLabel, authorLabel, genreLabel);

        // Evento click
        bookBox.setOnMouseClicked(e ->
            stateManager.setState(new BookDetailState(stateManager, book.getId()))
        );

        return bookBox;
    }
}