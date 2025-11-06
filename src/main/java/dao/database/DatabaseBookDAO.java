package dao.database;

import dao.BookDAO;
import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBookDAO implements BookDAO {

    private final DBConnection dbConnection;

    public DatabaseBookDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
            	books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getString("publisher"),
                        rs.getInt("pages"),
                        rs.getString("isbn"),
                        rs.getInt("stock"),
                        rs.getString("plot"),
                        rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public List<Book> getSearchedBooks(String searchText, String searchMode, String category, String year, boolean includeUnavailable) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE " +
                     "((? = 'title' AND LOWER(title) LIKE ?) " +
                     "OR (? = 'author' AND LOWER(author) LIKE ?)) " +
                     "AND ((? IS NULL OR ? = '') OR category = ?) " +
                     "AND ((? IS NULL OR ? = '') OR CAST(year AS CHAR) = ?) " +
                     "AND (? = 1 OR stock > 0)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + (searchText != null ? searchText.toLowerCase() : "") + "%";

            stmt.setString(1, searchMode);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchMode);
            stmt.setString(4, searchPattern);

            stmt.setString(5, category);
            stmt.setString(6, category);
            stmt.setString(7, category);

            stmt.setString(8, year);
            stmt.setString(9, year);
            stmt.setString(10, year);

            stmt.setInt(11, includeUnavailable ? 1 : 0);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    rs.getInt("year"),
                    rs.getString("publisher"),
                    rs.getInt("pages"),
                    rs.getString("isbn"),
                    rs.getInt("stock"),
                    rs.getString("plot"),
                    rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public Book getBookById(int id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Book(
                		rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getString("publisher"),
                        rs.getInt("pages"),
                        rs.getString("isbn"),
                        rs.getInt("stock"),
                        rs.getString("plot"),
                        rs.getString("image_path")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addBook(Book book) {
        String sql = "INSERT INTO books (id, title, author, category, year, publisher, pages, isbn, stock, plot, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
        	stmt.setInt(1, book.getId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getCategory());
            stmt.setInt(5, book.getYear());
            stmt.setString(6, book.getPublisher());
            stmt.setInt(7, book.getPages());
            stmt.setString(8, book.getIsbn());
            stmt.setInt(9, book.getStock());
            stmt.setString(10, book.getPlot());
            stmt.setString(11, book.getImagePath());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, category=?, year=?, publisher=?, pages=?, isbn=?, stock=?, plot=?, image_path=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
        	stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCategory());
            stmt.setInt(4, book.getYear());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPages());
            stmt.setString(7, book.getIsbn());
            stmt.setInt(8, book.getStock());
            stmt.setString(9, book.getPlot());
            stmt.setString(10, book.getImagePath());
            stmt.setInt(11, book.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBook(int id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}