package dao.csv;

import model.Book;
import dao.BookDAO;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CSVBookDAO implements BookDAO {

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
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }

    @Override
    public Book getBookById(int id) {
        return getAllBooks().stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
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
            e.printStackTrace();
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
            // intestazione opzionale
            // bw.write("id,title,author,category,year,publisher,pages,isbn,stock,plot,imagePath\n");

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
            e.printStackTrace();
        }
    }

	@Override
	public List<Book> getSearchedBooks(String searchText, String searchMode, String category, String yearFrom, String yearTo,
			boolean includeUnavailable) {
		// TODO Auto-generated method stub
		return null;
	}
}