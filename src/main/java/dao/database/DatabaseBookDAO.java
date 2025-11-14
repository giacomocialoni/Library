package dao.database;

import dao.BookDAO;
import dao.PurchaseDAO;
import dao.factory.DAOFactory;
import model.Book;
import model.Loan;
import model.Purchase;
import utils.LoanStatus;

import java.sql.*;
import java.time.LocalDate;
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
        String sql = "SELECT * FROM books";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addBook(Book book) {
        String sql = "INSERT INTO books (id, title, author, category, year, publisher, pages, isbn, stock, plot, image_path, price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillBookPreparedStatement(stmt, book);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, category=?, year=?, publisher=?, pages=?, isbn=?, stock=?, plot=?, image_path=?, price=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCategory());
            stmt.setInt(4, book.getYear());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPages());
            stmt.setString(7, book.getIsbn());
            stmt.setInt(8, book.getStock()); // QUESTO È IMPORTANTE - aggiorna lo stock
            stmt.setString(9, book.getPlot());
            stmt.setString(10, book.getImagePath());
            stmt.setDouble(11, book.getPrice());
            stmt.setInt(12, book.getId());
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Book> getSearchedBooks(String searchText, String searchMode, String category,
                                       String yearFrom, String yearTo, boolean includeUnavailable) {

        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE " +
                     "((? = 'title' AND LOWER(title) LIKE ?) " +
                     "OR (? = 'author' AND LOWER(author) LIKE ?)) " +
                     "AND ((? IS NULL OR ? = '') OR category = ?) " +
                     "AND ((? IS NULL OR ? = '') OR year >= CAST(? AS UNSIGNED)) " +
                     "AND ((? IS NULL OR ? = '') OR year <= CAST(? AS UNSIGNED)) " +
                     "AND (? = 1 OR stock > 0)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + (searchText != null ? searchText.toLowerCase() : "") + "%";

            stmt.setString(1, searchMode);
            stmt.setString(2, pattern);
            stmt.setString(3, searchMode);
            stmt.setString(4, pattern);

            stmt.setString(5, category);
            stmt.setString(6, category);
            stmt.setString(7, category);

            stmt.setString(8, yearFrom);
            stmt.setString(9, yearFrom);
            stmt.setString(10, yearFrom);

            stmt.setString(11, yearTo);
            stmt.setString(12, yearTo);
            stmt.setString(13, yearTo);

            stmt.setInt(14, includeUnavailable ? 1 : 0);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    @Override
    public List<Book> getPurchasedBooks(String userEmail) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM books b " +
                     "JOIN purchases p ON b.id = p.book_id " +
                     "WHERE p.user_email = ? AND p.status = 'PURCHASED'"; // AGGIUNTA CONDIZIONE STATUS
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // NUOVO METODO: Restituisce tutti i Purchase (per gestione admin)
    @Override
    public List<Purchase> getPurchasesByUser(String userEmail) {
        // Questo dovrebbe essere in PurchaseDAO, ma se serve qui:
        PurchaseDAO purchaseDAO = DAOFactory.getActiveFactory().getPurchaseDAO();
        return purchaseDAO.getPurchasesByUser(userEmail);
    }

    @Override
    public List<Loan> getLoanedBooks(String userEmail) {
        List<Loan> loans = new ArrayList<>();
        // CORREZIONE: Query corretta con tutti i campi
        String sql = "SELECT l.*, b.* FROM loans l " +
                     "JOIN books b ON l.book_id = b.id " +
                     "WHERE l.user_email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // CORREZIONE: Estrai tutti i campi corretti
                int loanId = rs.getInt("id");
                String email = rs.getString("user_email");
                Book book = extractBookFromResultSet(rs);
                
                LoanStatus status = LoanStatus.valueOf(rs.getString("status"));
                LocalDate reservedDate = rs.getDate("reserved_date") != null ? 
                    rs.getDate("reserved_date").toLocalDate() : null;
                LocalDate loanedDate = rs.getDate("loaned_date") != null ? 
                    rs.getDate("loaned_date").toLocalDate() : null;
                LocalDate returningDate = rs.getDate("returning_date") != null ? 
                    rs.getDate("returning_date").toLocalDate() : null;
                
                // CORREZIONE: Crea Loan con costruttore corretto
                Loan loan = new Loan(loanId, email, book, status, reservedDate, loanedDate, returningDate);
                loans.add(loan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // --- UTILITY PRIVATE ---

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
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
            rs.getString("image_path"),
            rs.getDouble("price")
        );
    }

    private void fillBookPreparedStatement(PreparedStatement stmt, Book book) throws SQLException {
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
    }
}