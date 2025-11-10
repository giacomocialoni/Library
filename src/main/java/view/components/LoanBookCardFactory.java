package view.components;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Loan;

public class LoanBookCardFactory {

    private final BookCardFactory cardFactory;

    public LoanBookCardFactory(BookCardFactory cardFactory) {
        this.cardFactory = cardFactory;
    }

    public VBox createLoanCard(Loan loan) {
        // Crea la card del libro (cliccabile)
        StackPane bookStack = cardFactory.createBookCard(loan.getBook());
        bookStack.setPrefWidth(150);

        // Label per inizio e fine prestito
        Label startLabel = new Label("Inizio: " + loan.getFromDate());
        startLabel.getStyleClass().add("loan-label");

        Label dueLabel = new Label("Scadenza: " + loan.getDueDate());
        dueLabel.getStyleClass().add("loan-label");

        // Calcola giorni rimanenti e gestisci testo senza segno meno
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), loan.getDueDate());
        Label daysLabel = new Label();
        
        if (daysRemaining < 0) {
            // Scaduto -> rosso
            daysLabel.setText("Scaduto " + Math.abs(daysRemaining) + " giorni fa");
            daysLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; fx-font-size: 12px;");
        } else if (daysRemaining == 0) {
            // Scade oggi
            daysLabel.setText("Scade oggi!");
            daysLabel.setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold; fx-font-size: 12px;");
        } else if (daysRemaining <= 3) {
            // 3 giorni o meno -> arancione
            daysLabel.setText("Scade tra " + daysRemaining + " giorni");
            daysLabel.setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold; fx-font-size: 12px;");
        } else {
            // Normale
            daysLabel.setText("Scade tra " + daysRemaining + " giorni");
            daysLabel.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold; fx-font-size: 12px;");
        }
        daysLabel.getStyleClass().add("remaining-time-loan");
        
        // VBox per le info del prestito sotto la book card
        VBox infoBox = new VBox(5, startLabel, dueLabel, daysLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(8, 5, 8, 5));
        infoBox.setStyle("-fx-background-color: #f8f4f0; -fx-background-radius: 8; -fx-border-color: #e8dad0; -fx-border-width: 1; -fx-border-radius: 8;");
        infoBox.setPrefWidth(160);
        infoBox.setMaxWidth(160);
        
        // Contenitore principale verticale (card + info)
        VBox container = new VBox(10, bookStack, infoBox);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(90, 74, 58, 0.08), 8, 0, 0, 2);");
        container.setPrefWidth(180);
        container.setMaxWidth(180);

        return container;
    }
}