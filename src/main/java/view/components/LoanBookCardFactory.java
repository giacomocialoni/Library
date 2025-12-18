package view.components;

import bean.LoanBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoanBookCardFactory {

    private final BookCardFactory cardFactory;

    public LoanBookCardFactory(BookCardFactory cardFactory) {
        this.cardFactory = cardFactory;
    }

    public VBox createLoanCard(LoanBean loan) {

        StackPane bookStack =
                cardFactory.createBookCard(loan.getBook());

        Label start = new Label(
                "Inizio: " +
                (loan.getLoanedDate() != null
                        ? loan.getLoanedDate()
                        : "N/D"));

        Label due = new Label(
                "Scadenza: " +
                (loan.getReturningDate() != null
                        ? loan.getReturningDate()
                        : "N/D"));

        Label remaining = new Label();
        long days = loan.daysRemaining();

        if (days < 0) remaining.setText("Scaduto");
        else remaining.setText("Giorni rimasti: " + days);

        VBox info = new VBox(5, start, due, remaining);
        info.setPadding(new Insets(8));
        info.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(10, bookStack, info);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(10));

        return container;
    }
}