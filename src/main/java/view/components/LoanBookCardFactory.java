package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Book;
import model.Loan;

public class LoanBookCardFactory {

    private final BookCardFactory cardFactory;

    public LoanBookCardFactory(BookCardFactory cardFactory) {
        this.cardFactory = cardFactory;
    }

    public VBox createLoanCard(Loan loan) {
        Book book = loan.getBook();

        // Usa il nuovo metodo "createLoanBookCard" che gestisce correttamente il click
        StackPane bookStack = cardFactory.createBookCard(book);
        bookStack.setPrefSize(200, 300);
        bookStack.setMinSize(200, 300);
        bookStack.setMaxSize(200, 300);

        // Label per inizio e fine prestito con controllo null
        String startDateText = loan.getLoanedDate() != null ? loan.getLoanedDate().toString() : "Non specificato";
        String dueDateText = loan.getReturningDate() != null ? loan.getReturningDate().toString() : "Non specificato";
        
        Label startLabel = new Label("Inizio: " + startDateText);
        startLabel.getStyleClass().add("loan-label");

        Label dueLabel = new Label("Scadenza: " + dueDateText);
        dueLabel.getStyleClass().add("loan-label");

        // Calcola giorni rimanenti con gestione sicura
        long daysRemaining = loan.daysRemaining();
        Label daysLabel = new Label();
        
        if (loan.getReturningDate() == null) {
            daysLabel.setText("Data non disponibile");
            daysLabel.setStyle("-fx-text-fill: #757575; -fx-font-weight: bold; fx-font-size: 12px;");
        } else if (daysRemaining < 0) {
            daysLabel.setText("Scaduto " + Math.abs(daysRemaining) + " giorni fa");
            daysLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; fx-font-size: 12px;");
        } else if (daysRemaining == 0) {
            daysLabel.setText("Scade oggi!");
            daysLabel.setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold; fx-font-size: 12px;");
        } else if (daysRemaining <= 3) {
            daysLabel.setText("Scade tra " + daysRemaining + " giorni");
            daysLabel.setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold; fx-font-size: 12px;");
        } else {
            daysLabel.setText("Scade tra " + daysRemaining + " giorni");
            daysLabel.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold; fx-font-size: 12px;");
        }
        daysLabel.getStyleClass().add("remaining-time-loan");
        
        // VBox per le info del prestito sotto la book card
        VBox infoBox = new VBox(5, startLabel, dueLabel, daysLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(8, 5, 8, 5));
        infoBox.setStyle("-fx-background-color: #f8f4f0; -fx-background-radius: 8; -fx-border-color: #e8dad0; -fx-border-width: 1; -fx-border-radius: 8;");
        infoBox.setPrefWidth(180);
        infoBox.setMaxWidth(180);
        
        // Contenitore principale verticale (card + info)
        VBox container = new VBox(10, bookStack, infoBox);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(90, 74, 58, 0.08), 8, 0, 0, 2);");
        container.setPrefWidth(200);
        container.setMaxWidth(200);

        return container;
    }
}