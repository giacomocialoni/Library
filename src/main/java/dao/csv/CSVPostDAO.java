package dao.csv;

import dao.PostDAO;
import model.Post;
import exception.DAOException;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVPostDAO implements PostDAO {
    
    private static final String FILE_PATH = "src/main/resources/data/posts.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public List<Post> getAllPostsOrderedByDate() throws DAOException {
        List<Post> posts = new ArrayList<>();
        
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return posts; // File non esiste, restituisci lista vuota
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                try {
                    // Usa un parser CSV che gestisca le virgolette
                    List<String> fields = parseCSVLine(line);
                    
                    if (fields.size() >= 4) {
                        // Il tuo formato: user_fk, title, content, post_date
                        String userEmail = fields.get(0);
                        String title = fields.get(1);
                        String content = fields.get(2);
                        
                        // Rimuovi le virgolette dalla data se presenti
                        String dateStr = fields.get(3).replace("\"", "").trim();
                        LocalDateTime postDate = LocalDateTime.parse(dateStr, DATE_FORMATTER);
                        
                        // Per CSV, non abbiamo author_name e role dal file
                        // Usiamo valori di default o li deriviamo dall'email
                        String authorName = getAuthorNameFromEmail(userEmail);
                        String role = userEmail.contains("admin") ? "admin" : "user";
                        
                        posts.add(new Post(userEmail, authorName, role, title, content, postDate));
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing del post: " + line);
                    e.printStackTrace();
                }
            }
            
            // Ordina per data discendente
            posts.sort((p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate()));
            
        } catch (IOException e) {
            throw new DAOException("Errore durante la lettura dei post da CSV", e);
        }
        
        return posts;
    }
    
    @Override
    public void addPost(Post post) throws DAOException {
        Path path = Paths.get(FILE_PATH);
        boolean fileExists = Files.exists(path);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, 
                StandardOpenOption.CREATE, 
                StandardOpenOption.APPEND)) {
            
            if (!fileExists || Files.size(path) == 0) {
                // Scrivi header nel formato del tuo file esistente
                writer.write("user_fk,title,content,post_date");
                writer.newLine();
            }
            
            // Formatta la data con le virgolette come nel file esistente
            String line = String.join(",",
                post.getUserEmail(),
                "\"" + post.getTitle().replace("\"", "\"\"") + "\"",
                "\"" + post.getContent().replace("\"", "\"\"") + "\"",
                "\"" + post.getPostDate().format(DATE_FORMATTER) + "\""
            );
            
            writer.write(line);
            writer.newLine();
            
        } catch (IOException e) {
            throw new DAOException("Errore durante il salvataggio del post in CSV", e);
        }
    }
    
    /**
     * Metodo di supporto per derivare il nome autore dall'email
     */
    private String getAuthorNameFromEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Autore sconosciuto";
        }
        
        // Estrai la parte prima della @
        String username = email.split("@")[0];
        
        // Capitalizza la prima lettera
        if (username.length() > 0) {
            return Character.toUpperCase(username.charAt(0)) + 
                   (username.length() > 1 ? username.substring(1) : "");
        }
        
        return "Autore sconosciuto";
    }
    
    /**
     * Parser CSV che gestisce le virgolette
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
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        
        // Aggiungi l'ultimo campo
        fields.add(currentField.toString());
        
        return fields;
    }
}