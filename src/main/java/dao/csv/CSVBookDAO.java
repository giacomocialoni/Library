package dao.csv;

import dao.BookDAO;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.Book;
import model.Loan;
import model.Purchase;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CSVBookDAO implements BookDAO {
    
    private static final String FILE_PATH = "data/book.csv";
    private final List<Book> books;
    
    public CSVBookDAO() throws DAOException {
        this.books = new ArrayList<>();
        loadBooks();
    }
    
    @Override
    public List<Book> getAllBooks() throws DAOException {
        return new ArrayList<>(books);
    }
    
    @Override
    public Book getBookById(int id) throws DAOException, RecordNotFoundException {
        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst()
                .orElseThrow(() -> 
                    new RecordNotFoundException("Libro con ID " + id + " non trovato"));
    }
    
    @Override
    public void addBook(Book book) throws DAOException {
        books.add(book);
        saveBooks();
    }
    
    @Override
    public void updateBook(Book book) throws DAOException, RecordNotFoundException {
        Book existingBook = getBookById(book.getId());
        books.remove(existingBook);
        books.add(book);
        saveBooks();
    }
    
    @Override
    public void deleteBook(int id) throws DAOException, RecordNotFoundException {
        Book book = getBookById(id);
        books.remove(book);
        saveBooks();
    }
    
    @Override
    public List<Book> getSearchedBooks(String searchText, String searchMode,
                                       String category, String yearFrom,
                                       String yearTo, boolean includeUnavailable) 
            throws DAOException {
        
        return books.stream()
                .filter(book -> includeUnavailable || book.getStock() > 0)
                .filter(book -> category == null || category.isEmpty() || 
                        book.getCategory().equalsIgnoreCase(category))
                .filter(book -> {
                    if (yearFrom != null && !yearFrom.isEmpty()) {
                        try {
                            int yearFromInt = Integer.parseInt(yearFrom);
                            if (book.getYear() < yearFromInt) return false;
                        } catch (NumberFormatException e) {
                            // Ignora formato non valido
                        }
                    }
                    return true;
                })
                .filter(book -> {
                    if (yearTo != null && !yearTo.isEmpty()) {
                        try {
                            int yearToInt = Integer.parseInt(yearTo);
                            if (book.getYear() > yearToInt) return false;
                        } catch (NumberFormatException e) {
                            // Ignora formato non valido
                        }
                    }
                    return true;
                })
                .filter(book -> matchSearch(book, searchText, searchMode))
                .toList();
    }
    
    @Override
    public List<Book> getPurchasedBooks(String userEmail) throws DAOException {
        // Invece di lanciare eccezione, restituisci lista vuota per CSV
        return new ArrayList<>();
    }
    
    @Override
    public List<Purchase> getPurchasesByUser(String userEmail) throws DAOException {
        // Per CSV, restituisci lista vuota
        return new ArrayList<>();
    }
    
    @Override
    public List<Loan> getLoanedBooks(String userEmail) throws DAOException {
        // Per CSV, restituisci lista vuota
        return new ArrayList<>();
    }
    
    private boolean matchSearch(Book book, String searchText, String searchMode) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }
        
        String text = searchText.toLowerCase().trim();
        
        if ("author".equalsIgnoreCase(searchMode)) {
            return book.getAuthor().toLowerCase().contains(text);
        } else { // title è il default
            return book.getTitle().toLowerCase().contains(text);
        }
    }
    
    private void loadBooks() throws DAOException {
        books.clear();
        
        try {
            // Prova a caricare dalla risorsa
            URL resource = getClass().getClassLoader().getResource(FILE_PATH);
            if (resource != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(resource.openStream()))) {
                    loadFromReader(reader);
                    return;
                }
            }
            
            // Prova a caricare dal filesystem
            Path path = Paths.get("src/main/resources", FILE_PATH);
            if (Files.exists(path)) {
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    loadFromReader(reader);
                    return;
                }
            }
            
            throw new DAOException("File books.csv non trovato in: " + FILE_PATH);
            
        } catch (IOException e) {
            throw new DAOException("Errore durante il caricamento dei libri", e);
        }
    }
    
    private void loadFromReader(BufferedReader reader) throws IOException {
        String line = reader.readLine(); // Skip header
        
        int lineNumber = 1;
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            try {
                Book book = parseBook(line, lineNumber);
                if (book != null) {
                    books.add(book);
                }
            } catch (Exception e) {
                System.err.println("Errore nel parsing della riga " + lineNumber + ": " + line);
                e.printStackTrace();
            }
            lineNumber++;
        }
    }
    
    private void saveBooks() throws DAOException {
        Path path = Paths.get("src/main/resources", FILE_PATH);
        
        try {
            Files.createDirectories(path.getParent());
            
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write("id,title,author,category,year,publisher,pages,isbn,stock,plot,image_path,price");
                writer.newLine();
                
                for (Book book : books) {
                    writer.write(formatBook(book));
                    writer.newLine();
                }
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante il salvataggio dei libri", e);
        }
    }
    
    private Book parseBook(String line, int lineNumber) {
        // Usa un parser CSV semplice che gestisca le virgolette
        List<String> fields = parseCSVLine(line);
        
        if (fields.size() < 12) {
            System.err.println("Linea " + lineNumber + ": Numero insufficiente di campi (" + fields.size() + " invece di 12)");
            return null;
        }
        
        try {
            // Ordine dei campi BASATO SUL TUO FILE CSV:
            // 0: id, 1: title, 2: author, 3: year, 4: plot, 5: image_path, 
            // 6: publisher, 7: pages, 8: isbn, 9: stock, 10: category, 11: price
            
            return new Book(
                Integer.parseInt(fields.get(0).trim()),      // id
                fields.get(1).trim(),                        // title
                fields.get(2).trim(),                        // author
                fields.get(10).trim(),                       // category (POSIZIONE 10)
                Integer.parseInt(fields.get(3).trim()),      // year (POSIZIONE 3)
                fields.get(6).trim(),                        // publisher (POSIZIONE 6)
                Integer.parseInt(fields.get(7).trim()),      // pages (POSIZIONE 7)
                fields.get(8).trim(),                        // isbn (POSIZIONE 8)
                Integer.parseInt(fields.get(9).trim()),      // stock (POSIZIONE 9)
                fields.get(4).trim(),                        // plot (POSIZIONE 4)
                fields.get(5).trim(),                        // image_path (POSIZIONE 5)
                Double.parseDouble(fields.get(11).trim())    // price (POSIZIONE 11)
            );
        } catch (NumberFormatException e) {
            System.err.println("Errore formato numerico in riga " + lineNumber + ": " + line);
            System.err.println("Campi: " + fields);
            return null;
        } catch (Exception e) {
            System.err.println("Errore generale in riga " + lineNumber + ": " + line);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Metodo di supporto per parsare correttamente una linea CSV
     * Gestisce virgolette e virgole all'interno dei campi
     */
    private List<String> parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                // Controlla se è una virgoletta doppia (escape)
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // Salta la prossima virgoletta
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0); // Reset
            } else {
                currentField.append(c);
            }
        }
        
        // Aggiungi l'ultimo campo
        fields.add(currentField.toString());
        
        return fields;
    }
    
    private String formatBook(Book book) {
        return String.join(",",
            String.valueOf(book.getId()),
            escapeComma(book.getTitle()),
            escapeComma(book.getAuthor()),
            escapeComma(book.getCategory()),
            String.valueOf(book.getYear()),
            escapeComma(book.getPublisher()),
            String.valueOf(book.getPages()),
            escapeComma(book.getIsbn()),
            String.valueOf(book.getStock()),
            escapeComma(book.getPlot()),
            escapeComma(book.getImagePath()),
            String.valueOf(book.getPrice())
        );
    }
    
    private String escapeComma(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}