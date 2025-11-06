package view.components;

import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Post;

public class PostCardFactory {

    public VBox createPostCard(Post post) {
        VBox postBox = new VBox(15);
        postBox.getStyleClass().add("post-card");
        postBox.setPadding(new Insets(15));

        // HEADER
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("post-header");

        Label authorLabel = new Label(post.getAuthorName());
        authorLabel.getStyleClass().add("post-author");
        header.getChildren().add(authorLabel);

        // Punto separatore
        Label dotLabel = new Label("•");
        dotLabel.setStyle("-fx-text-fill: #ccc;");
        header.getChildren().add(dotLabel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm    dd/MM/yyyy");
        Label dateLabel = new Label(post.getPostDate().format(formatter));
        dateLabel.getStyleClass().add("post-date");
        header.getChildren().add(dateLabel);

        // Contenuto
        Label contentLabel = new Label(post.getContent());
        contentLabel.getStyleClass().add("post-content");
        contentLabel.setWrapText(true);

        // Titolo opzionale
        if (post.getTitle() != null && !post.getTitle().isBlank()) {
            Label titleLabel = new Label(post.getTitle());
            titleLabel.getStyleClass().add("post-header");
            postBox.getChildren().addAll(header, new Separator(), titleLabel, contentLabel);
        } else {
            postBox.getChildren().addAll(header, new Separator(), contentLabel);
        }

        return postBox;
    }
}