package dao.database;

import dao.BookDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
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
    public List<Book> getAllBooks() throws DAOException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
            return books;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero di tutti i libri.", e);
        }
    }

    @Override
    public Book getBookById(int id) throws DAOException, RecordNotFoundException {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractBookFromResultSet(rs);
            } else {
                throw new RecordNotFoundException("Libro con ID " + id + " non trovato.");
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il caricamento del libro con ID " + id, e);
        }
    }

    @Override
    public void addBook(Book book) throws DAOException {
        String sql = "INSERT INTO books (id, title, author, category, year, publisher, pages, isbn, stock, plot, image_path, price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillBookPreparedStatement(stmt, book);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiunta del libro: " + book.getTitle(), e);
        }
    }

    @Override
    public void updateBook(Book book) throws DAOException, RecordNotFoundException {
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
            stmt.setInt(8, book.getStock());
            stmt.setString(9, book.getPlot());
            stmt.setString(10, book.getImagePath());
            stmt.setDouble(11, book.getPrice());
            stmt.setInt(12, book.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RecordNotFoundException("Impossibile aggiornare: nessun libro con ID " + book.getId());
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiornamento del libro ID " + book.getId(), e);
        }
    }

    @Override
    public void deleteBook(int id) throws DAOException, RecordNotFoundException {
        String sql = "DELETE FROM books WHERE id=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new RecordNotFoundException("Nessun libro trovato con ID " + id + " da eliminare.");
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'eliminazione del libro ID " + id, e);
        }
    }

    @Override
    public List<Book> getSearchedBooks(String searchText, String searchMode, String category,
                                       String yearFrom, String yearTo, boolean includeUnavailable)
            throws DAOException {

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

            return books;

        } catch (SQLException e) {
            throw new DAOException("Errore durante la ricerca dei libri.", e);
        }
    }

    @Override
    public List<Book> getPurchasedBooks(String userEmail) throws DAOException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM books b JOIN purchases p ON b.id = p.book_id WHERE p.user_email = ? AND p.status = 'PURCHASED'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }

            return books;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei libri acquistati per " + userEmail, e);
        }
    }

    @Override
    public List<Purchase> getPurchasesByUser(String userEmail) throws DAOException {
        try {
            // Utilizza il DAO Factory per ottenere il PurchaseDAO
            return DAOFactory.getActiveFactory().getPurchaseDAO().getPurchasesByUser(userEmail);
        } catch (Exception e) {
            throw new DAOException("Errore durante il recupero degli acquisti dell'utente " + userEmail, e);
        }
    }

    @Override
    public List<Loan> getLoanedBooks(String userEmail) throws DAOException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.* FROM loans l WHERE l.user_email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int loanId = rs.getInt("id");
                String email = rs.getString("user_email");
                int bookId = rs.getInt("book_id");
                LoanStatus status = LoanStatus.valueOf(rs.getString("status"));
                LocalDate reservedDate = rs.getDate("reserved_date") != null ?
                        rs.getDate("reserved_date").toLocalDate() : null;
                LocalDate loanedDate = rs.getDate("loaned_date") != null ?
                        rs.getDate("loaned_date").toLocalDate() : null;
                LocalDate returningDate = rs.getDate("returning_date") != null ?
                        rs.getDate("returning_date").toLocalDate() : null;

                loans.add(new Loan(loanId, email, bookId, reservedDate, loanedDate, returningDate, status));
            }

            return loans;

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei prestiti di " + userEmail, e);
        }
    }

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
        stmt.setDouble(12, book.getPrice());
    }
}