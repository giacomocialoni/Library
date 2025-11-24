package controller.gui;

import app.state.StateManager;
import app.state.ErrorState;
import app.state.SuccessState;
import controller.app.ReservationController;
import dao.BookDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Book;
import model.Loan;
import model.Purchase;
import view.components.ReservationCardFactory;

import java.util.List;

public class ReservationControllerGUI {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private VBox resultsContainer;
    @FXML private Label resultsLabel;
    @FXML private Label searchLabel;

    @FXML private CheckBox showSalesCheckbox;
    @FXML private CheckBox showLoansCheckbox;

    @FXML private Button userFilterButton;
    @FXML private Button bookFilterButton;

    private StateManager stateManager;
    private final ReservationController appController = new ReservationController();
    private final BookDAO bookDAO = DAOFactory.getActiveFactory().getBookDAO();
    private ReservationCardFactory cardFactory;
    
    private boolean initialized = false;
    private String searchMode = "user";

    public void setStateManager(StateManager stateManager) throws RecordNotFoundException, DAOException {
        this.stateManager = stateManager;
        this.cardFactory = new ReservationCardFactory();
        
        if (initialized) {
            loadAllReservations();
        }
    }

    @FXML
    public void initialize() {
        showSalesCheckbox.setSelected(true);
        showLoansCheckbox.setSelected(true);
        setUserFilter();
        initialized = true;
    }

    @FXML
    public void setUserFilter() {
        searchMode = "user";
        updateFilterButtons();
        searchLabel.setText("Cerca utente:");
        searchField.setPromptText("Inserisci email, nome o cognome utente");
    }

    @FXML
    public void setBookFilter() {
        searchMode = "book";
        updateFilterButtons();
        searchLabel.setText("Cerca libro:");
        searchField.setPromptText("Inserisci titolo o autore del libro");
    }

    private void updateFilterButtons() {
        userFilterButton.getStyleClass().remove("active");
        bookFilterButton.getStyleClass().remove("active");
        
        if ("user".equals(searchMode)) {
            userFilterButton.getStyleClass().add("active");
        } else {
            bookFilterButton.getStyleClass().add("active");
        }
    }

    @FXML
    public void handleSearch() throws RecordNotFoundException, DAOException {
        if (cardFactory == null) {
            showError("Attenzione", "Sistema non ancora inizializzato");
            return;
        }

        resultsContainer.getChildren().clear();

        String searchText = searchField.getText().trim();
        boolean includeSales = showSalesCheckbox.isSelected();
        boolean includeLoans = showLoansCheckbox.isSelected();

        if (searchText.isEmpty()) {
            loadAllReservations();
            return;
        }

        List<Purchase> purchases = List.of();
        List<Loan> loans = List.of();

        if (includeSales) {
            purchases = "user".equals(searchMode) ? 
                appController.searchPurchasesByUser(searchText) : 
                appController.searchPurchasesByBook(searchText);
        }

        if (includeLoans) {
            loans = "user".equals(searchMode) ? 
                appController.searchLoansByUser(searchText) : 
                appController.searchLoansByBook(searchText);
        }

        displayReservations(purchases, loans);
    }

    @FXML
    public void handleClearFilters() throws RecordNotFoundException, DAOException {
        searchField.clear();
        showSalesCheckbox.setSelected(true);
        showLoansCheckbox.setSelected(true);
        setUserFilter();
        loadAllReservations();
    }

    @FXML
    public void handleCheckboxChange() throws RecordNotFoundException, DAOException {
        if (cardFactory != null) {
            if (searchField.getText().trim().isEmpty()) {
                loadAllReservations();
            } else {
                handleSearch();
            }
        }
    }

    private void loadAllReservations() throws RecordNotFoundException, DAOException {
        if (cardFactory == null) return;

        resultsContainer.getChildren().clear();

        boolean includeSales = showSalesCheckbox.isSelected();
        boolean includeLoans = showLoansCheckbox.isSelected();

        List<Purchase> purchases = includeSales ? appController.getAllReservedPurchases() : List.of();
        List<Loan> loans = includeLoans ? appController.getAllReservedLoans() : List.of();

        displayReservations(purchases, loans);
    }

    private void displayReservations(List<Purchase> purchases, List<Loan> loans) throws RecordNotFoundException, DAOException {
        resultsContainer.getChildren().clear();

        int count = 0;

        for (Purchase purchase : purchases) {
            Book book = bookDAO.getBookById(purchase.getBookId());
            if (book != null) {
                var purchaseCard = cardFactory.createPurchaseCard(
                    purchase, 
                    book,
                    () -> handleAcceptPurchase(purchase.getId(), book.getId()),
                    () -> handleRejectPurchase(purchase.getId())
                );
                resultsContainer.getChildren().add(purchaseCard);
                count++;
            }
        }

        for (Loan loan : loans) {
            Book book = bookDAO.getBookById(loan.getBookId());
            if (book != null) {
                var loanCard = cardFactory.createLoanCard(
                    loan, 
                    book,
                    () -> handleAcceptLoan(loan.getId(), book.getId()),
                    () -> handleRejectLoan(loan.getId())
                );
                resultsContainer.getChildren().add(loanCard);
                count++;
            }
        }

        updateResultsLabel(count, purchases.size(), loans.size());
    }

    private void updateResultsLabel(int total, int salesCount, int loansCount) {
        String modeText = "user".equals(searchMode) ? "per utente" : "per libro";
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
            resultsLabel.setText("Prenotazioni trovate: " + total + " (Vendite: " + salesCount + ", Prestiti: " + loansCount + ")");
        } else {
            resultsLabel.setText("Trovate " + total + " prenotazioni " + modeText + " '" + searchText + "' (Vendite: " + salesCount + ", Prestiti: " + loansCount + ")");
        }
    }

    // ===== METODI MODIFICATI PER USARE GLI STATI =====

    private void handleAcceptPurchase(int purchaseId, int bookId) {
        try {
            appController.acceptPurchase(purchaseId);
            appController.updateBookStock(bookId, -1);
            loadAllReservations();
            showSuccess("Successo", "Vendita accettata con successo!");
        } catch (Exception e) {
            showError("Errore", "Errore nell'accettare la vendita: " + e.getMessage());
        }
    }

    private void handleRejectPurchase(int purchaseId) {
        try {
            // appController.rejectPurchase(purchaseId); // Se hai questo metodo
            loadAllReservations();
            showSuccess("Successo", "Vendita rifiutata!");
        } catch (Exception e) {
            showError("Errore", "Errore nel rifiutare la vendita: " + e.getMessage());
        }
    }

    private void handleAcceptLoan(int loanId, int bookId) {
        try {
            appController.acceptLoan(loanId);
            appController.updateBookStock(bookId, -1);
            loadAllReservations();
            showSuccess("Successo", "Prestito accettato con successo!");
        } catch (Exception e) {
            showError("Errore", "Errore nell'accettare il prestito: " + e.getMessage());
        }
    }

    private void handleRejectLoan(int loanId) {
        try {
            // appController.rejectLoan(loanId); // Se hai questo metodo
            loadAllReservations();
            showSuccess("Successo", "Prestito rifiutato!");
        } catch (Exception e) {
            showError("Errore", "Errore nel rifiutare il prestito: " + e.getMessage());
        }
    }

    // ===== NUOVI METODI PER GESTIONE STATI =====

    private void showSuccess(String title, String message) {
        SuccessState successState = new SuccessState(stateManager, message);
        stateManager.setState(successState);
    }

    private void showError(String title, String message) {
        ErrorState errorState = new ErrorState(stateManager, message);
        stateManager.setState(errorState);
    }
}