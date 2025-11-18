package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import model.Book;
import model.Purchase;
import model.Loan;
import app.state.StateManager;
import java.io.InputStream;

public class ReservationCardFactory {

    private final StateManager stateManager;

    public ReservationCardFactory(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public HBox createPurchaseCard(Purchase purchase, Book book, Runnable onAccept, Runnable onReject) {
        return createReservationCard(
            book,
            purchase.getUserEmail(),
            "Vendita",
            "Quantità: 1",
            "Prezzo: €" + String.format("%.2f", book.getPrice()),
            onAccept,
            onReject
        );
    }

    public HBox createLoanCard(Loan loan, Book book, Runnable onAccept, Runnable onReject) {
        return createReservationCard(
            book,
            loan.getUserEmail(),
            "Prestito",
            "Durata: 30 giorni",
            "Data prenotazione: " + loan.getReservedDate(),
            onAccept,
            onReject
        );
    }

    private HBox createReservationCard(Book book, String userEmail, String type, String detail1, String detail2, Runnable onAccept, Runnable onReject) {
		// Copertina del libro a sinistra
		ImageView coverImage = new ImageView();
		coverImage.setFitWidth(120);
		coverImage.setFitHeight(160);
		coverImage.setPreserveRatio(true);
		
		// Gestione dell'immagine con InputStream
		try {
		String imagePath = "/images/" + book.getImagePath();
		InputStream imageStream = getClass().getResourceAsStream(imagePath);
		if (imageStream == null) {
		System.err.println("Immagine non trovata: " + imagePath);
		imageStream = getClass().getResourceAsStream("/images/default.jpg");
		}
		
		if (imageStream != null) {
		Image image = new Image(imageStream);
		coverImage.setImage(image);
		
		// AGGIUNGI QUESTO PER GLI ANGOLI ARROTONDATI
		Rectangle clip = new Rectangle(coverImage.getFitWidth(), coverImage.getFitHeight());
		clip.setArcWidth(20);
		clip.setArcHeight(20);
		coverImage.setClip(clip);
		
		imageStream.close();
		} else {
		System.err.println("Impossibile caricare anche l'immagine di default");
		coverImage.setStyle("-fx-background-color: #e8dad0; -fx-border-color: #8b7355;");
		}
		} catch (Exception e) {
		System.err.println("Errore nel caricamento dell'immagine: " + e.getMessage());
		coverImage.setStyle("-fx-background-color: #e8dad0; -fx-border-color: #8b7355;");
		}
		
		// Container per l'immagine
		VBox imageContainer = new VBox(coverImage);
		imageContainer.setAlignment(Pos.CENTER);
		imageContainer.setPadding(new Insets(15));
		imageContainer.setMinWidth(150);
		imageContainer.setStyle("-fx-background-color: #faf8f5; -fx-background-radius: 10; -fx-border-radius: 10;");

		// Clip anche per il container
		Rectangle containerClip = new Rectangle(150, 190); // Larghezza e altezza del container
		containerClip.setArcWidth(20);
		containerClip.setArcHeight(20);
		imageContainer.setClip(containerClip);
		
		// Informazioni a destra
		VBox infoBox = new VBox(10);
		infoBox.setPadding(new Insets(20));
		infoBox.setAlignment(Pos.TOP_LEFT);
		
		Label userLabel = new Label("Utente: " + userEmail);
		userLabel.getStyleClass().add("reservation-user");
		
		Label titleLabel = new Label(book.getTitle());
		titleLabel.getStyleClass().add("reservation-title");
		
		Label authorLabel = new Label("di " + book.getAuthor());
		authorLabel.getStyleClass().add("reservation-author");
		
		Label detail1Label = new Label(detail1);
		detail1Label.getStyleClass().add("reservation-detail");
		
		Label detail2Label = new Label(detail2);
		detail2Label.getStyleClass().add("reservation-detail");
		
		// Pulsanti azione
		HBox buttonBox = new HBox(15);
		buttonBox.setAlignment(Pos.CENTER_LEFT);
		
		Button acceptButton = new Button(type);
		acceptButton.getStyleClass().add("buy-button");
		acceptButton.setOnAction(e -> onAccept.run());
		
		Button rejectButton = new Button("Rifiuta");
		rejectButton.getStyleClass().add("borrow-button");
		rejectButton.setOnAction(e -> onReject.run());
		
		buttonBox.getChildren().addAll(acceptButton, rejectButton);
		
		// Assembla info box
		infoBox.getChildren().addAll(
		userLabel, titleLabel, authorLabel, 
		detail1Label, detail2Label, buttonBox
		);
		
		// Card principale
		HBox card = new HBox(0);
		card.getStyleClass().add("reservation-card");
		card.setAlignment(Pos.CENTER_LEFT);
		card.getChildren().addAll(imageContainer, infoBox);
		
		return card;
		}
}