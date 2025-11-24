package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.User;
import dao.BookDAO;
import dao.LoanDAO;
import dao.PurchaseDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.Book;
import model.Loan;
import model.Purchase;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class ManageUsersCardFactory {

    private final BookDAO bookDAO;
    private final PurchaseDAO purchaseDAO;
    private final LoanDAO loanDAO;

    public ManageUsersCardFactory() {
        this.bookDAO = DAOFactory.getActiveFactory().getBookDAO();
        this.purchaseDAO = DAOFactory.getActiveFactory().getPurchaseDAO();
        this.loanDAO = DAOFactory.getActiveFactory().getLoanDAO();
    }

    public HBox createUserCard(User user, Runnable onRemoveUser) throws RecordNotFoundException, DAOException {
        
        // Informazioni utente (senza avatar)
        VBox infoBox = createUserInfo(user);
        
        // Controlli
        VBox controlsBox = createUserControls(user, onRemoveUser);
        
        // Card principale
        HBox card = new HBox(20);
        card.getStyleClass().add("user-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.getChildren().addAll(infoBox, controlsBox);
        
        return card;
    }

    private VBox createUserInfo(User user) throws RecordNotFoundException, DAOException {
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setPrefWidth(500);
        
        // Nome e cognome
        Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        nameLabel.getStyleClass().add("user-name");
        
        // Email
        Label emailLabel = new Label("Email: " + user.getEmail());
        emailLabel.getStyleClass().add("user-email");
        
        Separator separator1 = new Separator();
        
        // Ultimo acquisto
        Label lastPurchaseLabel = createLastPurchaseLabel(user.getEmail());
        lastPurchaseLabel.getStyleClass().add("user-activity");
        
        // Ultimo prestito
        Label lastLoanLabel = createLastLoanLabel(user.getEmail());
        lastLoanLabel.getStyleClass().add("user-activity");
        
        Separator separator2 = new Separator();
        
        // Statistiche
        Label statsLabel = createUserStatsLabel(user.getEmail());
        statsLabel.getStyleClass().add("user-stats");
        
        infoBox.getChildren().addAll(
            nameLabel, emailLabel, separator1, 
            lastPurchaseLabel, lastLoanLabel, separator2, statsLabel
        );
        
        return infoBox;
    }

    private VBox createUserControls(User user, Runnable onRemoveUser) {
        VBox controlsBox = new VBox(15);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10));
        
        // Pulsante elimina utente
        Button removeButton = new Button("Elimina Utente");
        removeButton.getStyleClass().add("remove-user-button");
        removeButton.setOnAction(e -> {
            if (confirmUserDeletion(user)) {
                onRemoveUser.run();
            }
        });
        
        controlsBox.getChildren().addAll(removeButton);
        
        return controlsBox;
    }

    private Label createLastPurchaseLabel(String userEmail) throws RecordNotFoundException, DAOException {
        List<Purchase> purchases = purchaseDAO.getPurchasesByUser(userEmail);
        
        if (purchases.isEmpty()) {
            return new Label("Ultimo acquisto: Nessun acquisto");
        }
        
        // Trova l'acquisto più recente
        Purchase lastPurchase = purchases.stream()
                .filter(p -> p.getPurchaseStatusDate() != null)
                .max(Comparator.comparing(Purchase::getPurchaseStatusDate))
                .orElse(purchases.get(0));
        
        Book book = bookDAO.getBookById(lastPurchase.getBookId());
        String bookTitle = book != null ? book.getTitle() : "Libro sconosciuto";
        
        String dateText = lastPurchase.getPurchaseStatusDate() != null ?
                lastPurchase.getPurchaseStatusDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Data non disponibile";
        
        return new Label("Ultimo acquisto: " + bookTitle + " (" + dateText + ")");
    }

    private Label createLastLoanLabel(String userEmail) throws RecordNotFoundException, DAOException {
        List<Loan> loans = loanDAO.getLoansByUser(userEmail);
        
        if (loans.isEmpty()) {
            return new Label("Ultimo prestito: Nessun prestito");
        }
        
        // Trova il prestito più recente
        Loan lastLoan = loans.stream()
                .filter(l -> l.getLoanedDate() != null)
                .max(Comparator.comparing(Loan::getLoanedDate))
                .orElse(loans.stream()
                        .filter(l -> l.getReservedDate() != null)
                        .max(Comparator.comparing(Loan::getReservedDate))
                        .orElse(loans.get(0)));
        
        Book book = bookDAO.getBookById(lastLoan.getBookId());
        String bookTitle = book != null ? book.getTitle() : "Libro sconosciuto";
        
        String dateText = lastLoan.getLoanedDate() != null ?
                lastLoan.getLoanedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                (lastLoan.getReservedDate() != null ?
                 lastLoan.getReservedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " (prenotato)" :
                 "Data non disponibile");
        
        return new Label("Ultimo prestito: " + bookTitle + " (" + dateText + ")");
    }

    private Label createUserStatsLabel(String userEmail) throws RecordNotFoundException, DAOException {
        List<Purchase> purchases = purchaseDAO.getPurchasesByUser(userEmail);
        List<Loan> loans = loanDAO.getLoansByUser(userEmail);
        
        long completedPurchases = purchases.stream()
                .filter(p -> p.getPurchaseStatusDate() != null)
                .count();
        
        long completedLoans = loans.stream()
                .filter(l -> l.getLoanedDate() != null && l.getReturningDate() != null)
                .count();
        
        long activeLoans = loans.stream()
                .filter(l -> l.getLoanedDate() != null && l.getReturningDate() == null)
                .count();
        
        long pendingReservations = loans.stream()
                .filter(l -> l.getLoanedDate() == null && l.getReservedDate() != null)
                .count();
        
        return new Label(String.format(
            "Statistiche: %d acquisti, %d prestiti completati, %d prestiti attivi, %d prenotazioni in sospeso",
            completedPurchases, completedLoans, activeLoans, pendingReservations
        ));
    }

    private boolean confirmUserDeletion(User user) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare: " + user.getFirstName() + " " + user.getLastName());
        alert.setContentText("Email: " + user.getEmail() + 
                           "\n\nQuesta operazione cancellerà:\n" +
                           "• Tutte le prenotazioni vendite\n" +
                           "• Tutte le prenotazioni prestiti\n" +
                           "• L'account utente\n\n" +
                           "L'operazione è IRREVERSIBILE. Continuare?");
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}