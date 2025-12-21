package controller.gui;

import app.state.StateManager;
import app.state.ErrorState;
import app.state.SuccessState;
import bean.BookBean;
import controller.app.ManageBooksController;
import dao.CategoryDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class CreateBookControllerGUI {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField yearField;
    @FXML private TextField publisherField;
    @FXML private TextField pagesField;
    @FXML private TextField isbnField;
    @FXML private TextField stockField;
    @FXML private TextArea plotArea;
    @FXML private TextField priceField;
    @FXML private Button imageButton;
    @FXML private ImageView previewImage;
    @FXML private Label imageLabel;
    @FXML private Button backButton;
    @FXML private Button createButton;
    @FXML private Button cancelButton;

    private StateManager stateManager;
    private final ManageBooksController appController = new ManageBooksController();
    private File selectedImageFile;
    private static final String IMAGE_UPLOAD_PATH = "src/main/resources/images/";
    private CategoryDAO categoryDAO;

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @FXML
    public void initialize() {
        try {
            // Inizializza DAO per le categorie
            categoryDAO = DAOFactory.getActiveFactory().getCategoryDAO();
            
            // Carica categorie dal database
            loadCategoriesFromDatabase();
            
        } catch (Exception e) {
            System.err.println("Errore nel caricamento delle categorie: " + e.getMessage());
            // Fallback a lista vuota
            categoryCombo.getItems().clear();
        }
        
        // Carica immagine placeholder
        try {
            Image placeholder = new Image(getClass().getResourceAsStream("/images/placeholder.png"));
            previewImage.setImage(placeholder);
        } catch (Exception e) {
            System.err.println("Placeholder image not found: " + e.getMessage());
        }
        
        // Validatori numerici
        yearField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                yearField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
        
        pagesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                pagesField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
        
        stockField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                stockField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
        
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                priceField.setText(newVal.replaceAll("[^\\d.]", ""));
            }
        });
    }

    private void loadCategoriesFromDatabase() {
        try {
            List<String> categories = new ArrayList<>();
            List<model.Category> dbCategories = categoryDAO.getAllCategories();
            
            for (model.Category cat : dbCategories) {
                categories.add(cat.getCategory());
            }
            
            categoryCombo.getItems().clear();
            categoryCombo.getItems().addAll(categories);
            
            if (!categories.isEmpty()) {
                categoryCombo.getSelectionModel().selectFirst();
            }
        } catch (DAOException e) {
            System.err.println("Errore nel caricamento delle categorie: " + e.getMessage());
            categoryCombo.getItems().clear();
        }
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona immagine del libro");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("Tutti i file", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            previewImage.setImage(new Image(file.toURI().toString()));
            imageLabel.setText(file.getName());
        }
    }

    @FXML
    private void handleCreateBook() {
        try {
            // Validazione
            if (!validateFields()) return;
            
            // Validazione categoria esistente
            String selectedCategory = categoryCombo.getValue();
            if (!isValidCategory(selectedCategory)) {
                stateManager.setState(new ErrorState(
                    stateManager,
                    "Categoria non valida. Seleziona una categoria esistente."
                ));
                return;
            }
            
            // Copia immagine se selezionata
            String imageFileName = null;
            if (selectedImageFile != null) {
                imageFileName = copyImageToResources(selectedImageFile);
            }
            
            // Crea BookBean
            BookBean bookBean = new BookBean();
            bookBean.setTitle(titleField.getText().trim());
            bookBean.setAuthor(authorField.getText().trim());
            bookBean.setCategory(selectedCategory);
            bookBean.setYear(Integer.parseInt(yearField.getText()));
            bookBean.setPublisher(publisherField.getText().trim());
            bookBean.setPages(Integer.parseInt(pagesField.getText()));
            bookBean.setIsbn(isbnField.getText().trim());
            bookBean.setStock(Integer.parseInt(stockField.getText()));
            bookBean.setPlot(plotArea.getText().trim());
            bookBean.setPrice(Double.parseDouble(priceField.getText()));
            bookBean.setImagePath(imageFileName); // Solo nome file, senza percorso
            
            // Crea Model Book
            model.Book book = new model.Book(
                0, // ID sarà generato dal DB
                bookBean.getTitle(),
                bookBean.getAuthor(),
                bookBean.getCategory(),
                bookBean.getYear(),
                bookBean.getPublisher(),
                bookBean.getPages(),
                bookBean.getIsbn(),
                bookBean.getStock(),
                bookBean.getPlot(),
                bookBean.getImagePath(), // Solo nome file, senza percorso
                bookBean.getPrice()
            );
            
            // Salva nel database
            appController.addBook(book);
            
            // Successo
            stateManager.setState(new SuccessState(
                stateManager, 
                "Libro '" + book.getTitle() + "' creato con successo!"
            ));
            
        } catch (Exception e) {
            stateManager.setState(new ErrorState(
                stateManager,
                "Errore nella creazione del libro: " + e.getMessage()
            ));
        }
    }

    @FXML
    private void handleCancel() {
        stateManager.goBack();
    }

    private boolean isValidCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        
        try {
            List<model.Category> categories = categoryDAO.getAllCategories();
            for (model.Category cat : categories) {
                if (cat.getCategory().equalsIgnoreCase(category.trim())) {
                    return true;
                }
            }
            return false;
        } catch (DAOException e) {
            return false;
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (titleField.getText().trim().isEmpty()) errors.append("• Titolo obbligatorio\n");
        if (authorField.getText().trim().isEmpty()) errors.append("• Autore obbligatorio\n");
        if (categoryCombo.getValue() == null) errors.append("• Categoria obbligatoria\n");
        if (yearField.getText().trim().isEmpty()) errors.append("• Anno obbligatorio\n");
        if (publisherField.getText().trim().isEmpty()) errors.append("• Editore obbligatorio\n");
        if (pagesField.getText().trim().isEmpty()) errors.append("• Pagine obbligatorie\n");
        if (isbnField.getText().trim().isEmpty()) errors.append("• ISBN obbligatorio\n");
        if (stockField.getText().trim().isEmpty()) errors.append("• Stock obbligatorio\n");
        if (priceField.getText().trim().isEmpty()) errors.append("• Prezzo obbligatorio\n");
        
        // Validazioni numeriche
        try {
            int year = Integer.parseInt(yearField.getText());
            if (year <= 0) errors.append("• Anno non valido\n");
        } catch (NumberFormatException e) {
            errors.append("• Anno non valido\n");
        }
        
        try {
            int pages = Integer.parseInt(pagesField.getText());
            if (pages <= 0) errors.append("• Pagine non valide\n");
        } catch (NumberFormatException e) {
            errors.append("• Pagine non valide\n");
        }
        
        try {
            int stock = Integer.parseInt(stockField.getText());
            if (stock < 0) errors.append("• Stock non valido\n");
        } catch (NumberFormatException e) {
            errors.append("• Stock non valido\n");
        }
        
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 0) errors.append("• Prezzo non valido\n");
        } catch (NumberFormatException e) {
            errors.append("• Prezzo non valido\n");
        }
        
        if (errors.length() > 0) {
            stateManager.setState(new ErrorState(
                stateManager,
                "Correggi i seguenti errori:\n" + errors.toString()
            ));
            return false;
        }
        
        return true;
    }

    private String copyImageToResources(File sourceFile) {
        try {
            // Crea directory se non esiste
            Path uploadDir = Path.of(IMAGE_UPLOAD_PATH);
            Files.createDirectories(uploadDir);
            
            // Estrai estensione del file
            String originalName = sourceFile.getName();
            String extension = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalName.substring(dotIndex);
            }
            
            // Genera nome univoco mantenendo l'estensione originale
            String fileName = System.currentTimeMillis() + "_book" + extension;
            Path targetPath = uploadDir.resolve(fileName);
            
            // Copia file
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Restituisce SOLO il nome del file (senza percorso)
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Errore nel caricamento dell'immagine: " + e.getMessage(), e);
        }
    }
}