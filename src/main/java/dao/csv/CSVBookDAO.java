package dao.csv;

import model.Book;
import model.Loan;
import model.Purchase;
import dao.BookDAO;
import exception.DAOException;
import exception.RecordNotFoundException;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVBookDAO implements BookDAO {

    private static final Logger logger = LoggerFactory.getLogger(CSVBookDAO.class);
    private static final String FILE_PATH = "src/main/resources/data/books.csv";

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Evita righe vuote o di intestazione
                if (line.trim().isEmpty() || line.startsWith("id,")) continue;

                String[] fields = line.split(",", -1); // -1 per mantenere i campi vuoti

                books.add(new Book(
                        Integer.parseInt(fields[0]),      // id
                        fields[1],                        // title
                        fields[2],                        // author
                        fields[3],                        // category
                        Integer.parseInt(fields[4]),      // year
                        fields[5],                        // publisher
                        Integer.parseInt(fields[6]),      // pages
                        fields[7],                        // isbn
                        Integer.parseInt(fields[8]),      // stock
                        fields[9],                        // plot
                        fields[10]                        // imagePath
, 0
                        
                ));
            }
        } catch (IOException e) {
            logger.error("Errore durante la lettura del file CSV: {}", FILE_PATH, e);
        }

        return books;
    }

    @Override
    public void addBook(Book book) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(String.format(
                    "%d,%s,%s,%s,%d,%s,%d,%s,%d,%s,%s%n",
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getYear(),
                    book.getPublisher(),
                    book.getPages(),
                    book.getIsbn(),
                    book.getStock(),
                    book.getPlot(),
                    book.getImagePath()
            ));
        } catch (IOException e) {
            logger.error("Errore durante l'aggiunta del libro al CSV: {}", book.getTitle(), e);
        }
    }

    @Override
    public void updateBook(Book book) {
        List<Book> books = getAllBooks();
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                break;
            }
        }
        saveAll(books);
    }

    @Override
    public void deleteBook(int id) {
        List<Book> books = getAllBooks();
        books.removeIf(b -> b.getId() == id);
        saveAll(books);
    }

    private void saveAll(List<Book> books) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Book b : books) {
                bw.write(String.format(
                        "%d,%s,%s,%s,%d,%s,%d,%s,%d,%s,%s%n",
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getCategory(),
                        b.getYear(),
                        b.getPublisher(),
                        b.getPages(),
                        b.getIsbn(),
                        b.getStock(),
                        b.getPlot(),
                        b.getImagePath()
                ));
            }
        } catch (IOException e) {
            logger.error("Errore durante il salvataggio dei libri nel CSV", e);
        }
    }


	@Override
	public List<Book> getSearchedBooks(String searchText, String searchMode, String category, String yearFrom, String yearTo,
			boolean includeUnavailable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Book> getPurchasedBooks(String userEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Loan> getLoanedBooks(String userEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Purchase> getPurchasesByUser(String userEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Book getBookById(int id) throws DAOException, RecordNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}