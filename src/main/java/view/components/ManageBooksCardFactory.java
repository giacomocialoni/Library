package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import model.Book;
import app.state.StateManager;
import java.io.InputStream;
import java.util.function.Consumer;

public class ManageBooksCardFactory {

    private final StateManager stateManager;

    public ManageBooksCardFactory(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public HBox createBookCard(Book book, Consumer<Integer> onIncreaseStock, 
                              Consumer<Integer> onDecreaseStock, Runnable onRemoveBook) {
        
        // Copertina del libro a sinistra
        ImageView coverImage = createBookCover(book);
        
        // Container per l'immagine
        VBox imageContainer = createImageContainer(coverImage);
        
        // Informazioni del libro
        VBox infoBox = createBookInfo(book);
        
        // Controlli stock
        VBox controlsBox = createStockControls(book, onIncreaseStock, onDecreaseStock, onRemoveBook);
        
        // Card principale
        HBox card = new HBox(20);
        card.getStyleClass().add("book-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.getChildren().addAll(imageContainer, infoBox, controlsBox);
        
        return card;
    }

    private ImageView createBookCover(Book book) {
        ImageView coverImage = new ImageView();
        coverImage.setFitWidth(100);
        coverImage.setFitHeight(140);
        coverImage.setPreserveRatio(true);
        
        try {
            String imagePath = "/images/" + book.getImagePath();
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream == null) {
                imageStream = getClass().getResourceAsStream("/images/default.jpg");
            }
            
            if (imageStream != null) {
                Image image = new Image(imageStream);
                coverImage.setImage(image);
                
                Rectangle clip = new Rectangle(coverImage.getFitWidth(), coverImage.getFitHeight());
                clip.setArcWidth(15);
                clip.setArcHeight(15);
                coverImage.setClip(clip);
                
                imageStream.close();
            } else {
                coverImage.setStyle("-fx-background-color: #e8dad0; -fx-border-color: #8b7355;");
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'immagine: " + e.getMessage());
            coverImage.setStyle("-fx-background-color: #e8dad0; -fx-border-color: #8b7355;");
        }
        
        return coverImage;
    }

    private VBox createImageContainer(ImageView coverImage) {
        VBox imageContainer = new VBox(coverImage);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPadding(new Insets(10));
        imageContainer.setMinWidth(120);
        imageContainer.setStyle("-fx-background-color: #faf8f5; -fx-background-radius: 10; -fx-border-radius: 10;");

        Rectangle containerClip = new Rectangle(120, 160);
        containerClip.setArcWidth(15);
        containerClip.setArcHeight(15);
        imageContainer.setClip(containerClip);
        
        return imageContainer;
    }

    private VBox createBookInfo(Book book) {
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setPrefWidth(300);
        
        Label titleLabel = new Label(book.getTitle());
        titleLabel.getStyleClass().add("book-title");
        titleLabel.setWrapText(true);
        
        Label authorLabel = new Label("di " + book.getAuthor());
        authorLabel.getStyleClass().add("book-author");
        
        Label isbnLabel = new Label("ISBN: " + book.getIsbn());
        isbnLabel.getStyleClass().add("book-detail");
        
        Label categoryLabel = new Label("Categoria: " + book.getCategory());
        categoryLabel.getStyleClass().add("book-detail");
        
        Label yearLabel = new Label("Anno: " + book.getYear());
        yearLabel.getStyleClass().add("book-detail");
        
        Label stockLabel = new Label("Stock attuale: " + book.getStock());
        stockLabel.getStyleClass().add("book-stock");
        
        Label priceLabel = new Label("Prezzo: €" + String.format("%.2f", book.getPrice()));
        priceLabel.getStyleClass().add("book-price");
        
        infoBox.getChildren().addAll(
            titleLabel, authorLabel, isbnLabel, 
            categoryLabel, yearLabel, stockLabel, priceLabel
        );
        
        return infoBox;
    }

    private VBox createStockControls(Book book, Consumer<Integer> onIncreaseStock, 
                                    Consumer<Integer> onDecreaseStock, Runnable onRemoveBook) {
        VBox controlsBox = new VBox(15);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10));
        
        // Spinner per quantità
        VBox quantityBox = new VBox(5);
        quantityBox.setAlignment(Pos.CENTER);
        
        Label quantityLabel = new Label("Quantità:");
        quantityLabel.getStyleClass().add("control-label");
        
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.getStyleClass().add("quantity-spinner");
        
        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner);
        
        // Pulsanti azione stock
        HBox stockButtons = new HBox(10);
        stockButtons.setAlignment(Pos.CENTER);
        
        Button addButton = new Button("Aggiungi");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(e -> {
            int quantity = quantitySpinner.getValue();
            onIncreaseStock.accept(quantity);
        });
        
        Button sellButton = new Button("Vendi");
        sellButton.getStyleClass().add("sell-button");
        sellButton.setOnAction(e -> {
            int quantity = quantitySpinner.getValue();
            onDecreaseStock.accept(quantity);
        });
        
        stockButtons.getChildren().addAll(addButton, sellButton);
        
        // Pulsante rimuovi libro
        Button removeButton = new Button("Rimuovi Libro");
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(e -> onRemoveBook.run());
        
        controlsBox.getChildren().addAll(quantityBox, stockButtons, removeButton);
        
        return controlsBox;
    }
}