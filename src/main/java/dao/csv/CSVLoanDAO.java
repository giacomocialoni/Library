package dao.csv;

import dao.LoanDAO;
import model.Loan;
import utils.Constants;
import utils.LoanStatus;
import exception.DAOException;
import exception.RecordNotFoundException;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVLoanDAO implements LoanDAO {
    
    private static final String FILE_PATH = "src/main/resources/data/loans.csv";
    
    @Override
    public void addLoan(String userEmail, int bookId) throws DAOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            
            if (Files.size(Paths.get(FILE_PATH)) == 0) {
                writer.write("id,user_email,book_id,status,reserved_date,loaned_date,returning_date");
                writer.newLine();
            }
            
            int nextId = getNextId();
            String line = String.join(",",
                String.valueOf(nextId),
                userEmail,
                String.valueOf(bookId),
                "RESERVED",
                LocalDate.now().toString(),
                "", // loaned_date vuoto
                ""  // returning_date vuoto
            );
            
            writer.write(line);
            writer.newLine();
            
        } catch (IOException e) {
            throw new DAOException("Errore durante l'aggiunta del prestito in CSV", e);
        }
    }
    
    @Override
    public List<Loan> getActiveLoansByUser(String userEmail) throws DAOException {
        return filterLoans(l -> l.getUserEmail().equals(userEmail) &&
                (l.getStatus() == LoanStatus.LOANED || l.getStatus() == LoanStatus.EXPIRED));
    }
    
    @Override
    public List<Loan> getReservedLoansByUser(String userEmail) throws DAOException, RecordNotFoundException {
        List<Loan> loans = filterLoans(l -> l.getUserEmail().equals(userEmail) && 
                l.getStatus() == LoanStatus.RESERVED);
        
        if (loans.isEmpty()) {
            throw new RecordNotFoundException("Nessun prestito riservato trovato per: " + userEmail);
        }
        
        return loans;
    }
    
    @Override
    public void acceptedLoan(int loanId) throws DAOException, RecordNotFoundException {
        updateLoanStatus(loanId, "LOANED", () -> {
            LocalDate today = LocalDate.now();
            LocalDate returningDate = today.plusDays(Constants.LOANING_DAYS);
            return new String[]{today.toString(), returningDate.toString()};
        });
    }
    
    @Override
    public void returnLoan(int loanId) throws DAOException, RecordNotFoundException {
        updateLoanStatus(loanId, "RETURNED", () -> new String[]{"", ""});
    }
    
    @Override
    public List<Loan> getLoansByUser(String userEmail) throws DAOException {
        return filterLoans(l -> l.getUserEmail().equals(userEmail));
    }
    
    @Override
    public List<Loan> getAllReservedLoans() throws DAOException {
        return filterLoans(l -> l.getStatus() == LoanStatus.RESERVED);
    }
    
    @Override
    public List<Loan> searchLoansByUser(String searchText) throws DAOException {
        String lowerSearch = searchText.toLowerCase();
        return filterLoans(l -> l.getStatus() == LoanStatus.LOANED && 
                l.getUserEmail().toLowerCase().contains(lowerSearch));
    }
    
    @Override
    public List<Loan> searchLoansByBook(String searchText) throws DAOException {
        // In CSV senza join, non possiamo cercare per titolo libro
        return new ArrayList<>();
    }
    
    @Override
    public List<Loan> getAllLoanedLoans() throws DAOException {
        return filterLoans(l -> l.getStatus() == LoanStatus.LOANED || l.getStatus() == LoanStatus.EXPIRED);
    }
    
    private List<Loan> filterLoans(java.util.function.Predicate<Loan> filter) throws DAOException {
        List<Loan> loans = new ArrayList<>();
        
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return loans;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            reader.readLine(); // Skip header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                try {
                    Loan loan = parseLoan(line);
                    if (loan != null && filter.test(loan)) {
                        loans.add(loan);
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing del prestito: " + line);
                    e.printStackTrace();
                }
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante la lettura dei prestiti da CSV", e);
        }
        
        return loans;
    }
    
    private Loan parseLoan(String line) {
        String[] fields = line.split(",", -1);
        
        try {
            int id = Integer.parseInt(fields[0]);
            String userEmail = fields[1];
            int bookId = Integer.parseInt(fields[2]);
            
            // ATTENZIONE: status è nella posizione 3, non 6!
            LoanStatus status = LoanStatus.valueOf(fields[3]);
            
            // I campi data sono spostati di una posizione
            LocalDate reservedDate = "NULL".equals(fields[4]) || fields[4].isEmpty() ? null : LocalDate.parse(fields[4]);
            LocalDate loanedDate = "NULL".equals(fields[5]) || fields[5].isEmpty() ? null : LocalDate.parse(fields[5]);
            LocalDate returningDate = "NULL".equals(fields[6]) || fields[6].isEmpty() ? null : LocalDate.parse(fields[6]);
            
            return new Loan(id, userEmail, bookId, reservedDate, loanedDate, returningDate, status);
            
        } catch (Exception e) {
            System.err.println("Errore nel parsing della riga: " + line);
            System.err.println("Campi trovati: " + java.util.Arrays.toString(fields));
            throw e;
        }
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
                try {
                    String[] fields = line.split(",");
                    int id = Integer.parseInt(fields[0]);
                    if (id > maxId) maxId = id;
                } catch (Exception e) {
                    // Ignora righe non valide
                }
            }
        }
        
        return maxId + 1;
    }
    
    private void updateLoanStatus(int loanId, String newStatus, DateSupplier dateSupplier) 
            throws DAOException, RecordNotFoundException {
        
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            lines.add(reader.readLine()); // Keep header
            
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] fields = line.split(",", -1);
                
                if (Integer.parseInt(fields[0]) == loanId) {
                    found = true;
                    fields[3] = newStatus; // status in posizione 3
                    
                    String[] dates = dateSupplier.get();
                    if (dates.length >= 2) {
                        if (fields[5].isEmpty() || "NULL".equals(fields[5])) fields[5] = dates[0]; // loaned_date in pos 5
                        if (fields[6].isEmpty() || "NULL".equals(fields[6])) fields[6] = dates[1]; // returning_date in pos 6
                    }
                    
                    line = String.join(",", fields);
                }
                
                lines.add(line);
            }
            
        } catch (IOException e) {
            throw new DAOException("Errore durante l'aggiornamento del prestito", e);
        }
        
        if (!found) {
            throw new RecordNotFoundException("Prestito non trovato con ID: " + loanId);
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
    
    @FunctionalInterface
    private interface DateSupplier {
        String[] get();
    }
}