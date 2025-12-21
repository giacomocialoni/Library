package dao.csv;

import dao.PurchaseDAO;
import model.Purchase;
import utils.PurchaseStatus;
import exception.DAOException;
import exception.RecordNotFoundException;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVPurchaseDAO implements PurchaseDAO {
    
    private static final String FILE_PATH = "src/main/resources/data/purchases.csv";
    
    @Override
    public void addReservedPurchase(String userEmail, int bookId) throws DAOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            
            if (Files.size(Paths.get(FILE_PATH)) == 0) {
                writer.write("id,user_email,book_id,status_date,status");
                writer.newLine();
            }
            
            int nextId = getNextId();
            String line = String.join(",",
                String.valueOf(nextId),
                userEmail,
                String.valueOf(bookId),
                LocalDate.now().toString(),
                "RESERVED"
            );
            
            writer.write(line);
            writer.newLine();
            
        } catch (IOException e) {
            throw new DAOException("Errore durante l'aggiunta dell'acquisto riservato", e);
        }
    }
    
    @Override
    public List<Purchase> getPurchasesByUser(String userEmail) throws DAOException {
        return filterPurchases(p -> p.getUserEmail().equals(userEmail));
    }
    
    @Override
    public List<Integer> getPurchasedBookIdsByUser(String userEmail) throws DAOException {
        List<Integer> bookIds = new ArrayList<>();
        
        for (Purchase purchase : filterPurchases(p -> 
                p.getUserEmail().equals(userEmail) && 
                p.getStatus() == PurchaseStatus.PURCHASED)) {
            bookIds.add(purchase.getBookId());
        }
        
        return bookIds;
    }
    
    @Override
    public boolean hasUserPurchasedBook(String userEmail, int bookId) throws DAOException, RecordNotFoundException {
        List<Purchase> purchases = filterPurchases(p -> 
                p.getUserEmail().equals(userEmail) && 
                p.getBookId() == bookId && 
                p.getStatus() == PurchaseStatus.PURCHASED);
        
        if (purchases.isEmpty()) {
            throw new RecordNotFoundException("L'utente " + userEmail + 
                " non ha acquistato il libro con ID " + bookId);
        }
        
        return true;
    }
    
    @Override
    public void acceptPurchase(int purchaseId) throws DAOException, RecordNotFoundException {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            lines.add(reader.readLine()); // Keep header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] fields = line.split(",", -1);
                
                if (Integer.parseInt(fields[0]) == purchaseId) {
                    found = true;
                    fields[3] = LocalDate.now().toString(); // status_date
                    fields[4] = "PURCHASED";
                    line = String.join(",", fields);
                }
                
                lines.add(line);
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante l'accettazione dell'acquisto", e);
        }
        
        if (!found) {
            throw new RecordNotFoundException("Acquisto non trovato con ID: " + purchaseId);
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante la scrittura del file CSV", e);
        }
    }
    
    @Override
    public List<Purchase> getAllPurchases() throws DAOException {
        return filterPurchases(p -> true);
    }
    
    @Override
    public List<Purchase> searchReservedPurchasesByUser(String searchText) throws DAOException {
        String lowerSearch = searchText.toLowerCase();
        return filterPurchases(p -> p.getStatus() == PurchaseStatus.RESERVED &&
                p.getUserEmail().toLowerCase().contains(lowerSearch));
    }
    
    @Override
    public List<Purchase> searchReservedPurchasesByBook(String searchText) throws DAOException {
        // In CSV senza join, non possiamo cercare per titolo/autore
        return new ArrayList<>();
    }
    
    private List<Purchase> filterPurchases(java.util.function.Predicate<Purchase> filter) throws DAOException {
        List<Purchase> purchases = new ArrayList<>();
        
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return purchases;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            reader.readLine(); // Skip header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                Purchase purchase = parsePurchase(line);
                if (filter.test(purchase)) {
                    purchases.add(purchase);
                }
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante la lettura degli acquisti da CSV", e);
        }
        
        return purchases;
    }
    
    private Purchase parsePurchase(String line) {
        String[] fields = line.split(",", -1);
        
        int id = Integer.parseInt(fields[0]);
        String userEmail = fields[1];
        int bookId = Integer.parseInt(fields[2]);
        
        // ATTENZIONE: Ordine campi diverso!
        // Tuo file: id,user_email,book_id,status,status_date
        PurchaseStatus status = PurchaseStatus.valueOf(fields[3]);
        LocalDate statusDate = fields[4].isEmpty() ? null : LocalDate.parse(fields[4]);
        
        return new Purchase(id, userEmail, bookId, statusDate, status);
    }
    
    private int getNextId() throws IOException {
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return 1;
        }
        
        int maxId = 0;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            reader.readLine(); // Skip header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] fields = line.split(",");
                int id = Integer.parseInt(fields[0]);
                if (id > maxId) maxId = id;
            }
        }
        
        return maxId + 1;
    }
}