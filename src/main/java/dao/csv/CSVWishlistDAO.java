package dao.csv;

import dao.WishlistDAO;
import model.User;
import model.Wishlist;
import exception.DAOException;
import exception.RecordNotFoundException;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CSVWishlistDAO implements WishlistDAO {
    
    private static final String FILE_PATH = "src/main/resources/data/wishlist.csv";
    
    @Override
    public void addToWishlist(String userEmail, int bookId) throws DAOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            
            if (Files.size(Paths.get(FILE_PATH)) == 0) {
                writer.write("user_email,book_id");
                writer.newLine();
            }
            
            String line = userEmail + "," + bookId;
            writer.write(line);
            writer.newLine();
            
        } catch (IOException e) {
            throw new DAOException("Errore durante l'aggiunta alla wishlist", e);
        }
    }
    
    @Override
    public void removeFromWishlist(String userEmail, int bookId) throws DAOException {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            lines.add(reader.readLine()); // Keep header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] fields = line.split(",");
                if (fields[0].equals(userEmail) && Integer.parseInt(fields[1]) == bookId) {
                    found = true;
                    continue; // Skip this line (remove)
                }
                lines.add(line);
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante la rimozione dalla wishlist", e);
        }
        
        if (!found) {
            return; // Non esiste, non fare nulla
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
    public boolean isInWishlist(String userEmail, int bookId) throws DAOException {
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return false;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            reader.readLine(); // Skip header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] fields = line.split(",");
                if (fields[0].equals(userEmail) && Integer.parseInt(fields[1]) == bookId) {
                    return true;
                }
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante il controllo della wishlist", e);
        }
        
        return false;
    }
    
    @Override
    public List<Wishlist> getWishlistByUser(String userEmail) throws DAOException, RecordNotFoundException {
        List<Wishlist> wishlist = new ArrayList<>();
        
        if (!Files.exists(Paths.get(FILE_PATH))) {
            throw new RecordNotFoundException("Wishlist vuota per l'utente: " + userEmail);
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            reader.readLine(); // Skip header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] fields = line.split(",");
                if (fields[0].equals(userEmail)) {
                    wishlist.add(new Wishlist(fields[0], Integer.parseInt(fields[1])));
                }
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante il recupero della wishlist", e);
        }
        
        if (wishlist.isEmpty()) {
            throw new RecordNotFoundException("Wishlist vuota per l'utente: " + userEmail);
        }
        
        return wishlist;
    }
    
    @Override
    public List<User> getUsersWithBookInWishlist(int bookId) throws DAOException {
        List<User> users = new ArrayList<>();
        
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return users;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            reader.readLine(); // Skip header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] fields = line.split(",");
                if (Integer.parseInt(fields[1]) == bookId) {
                    // Dovremmo avere un riferimento a UserDAO per ottenere i dettagli utente
                    // Per ora restituiamo utenti parziali
                    users.add(new User(fields[0], "", "", ""));
                }
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante il recupero degli utenti con libro in wishlist", e);
        }
        
        return users;
    }
}