package controller.observer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import model.Book;
import model.Wishlist;
import service.EmailService;
import dao.WishlistDAO;
import exception.DAOException;
import exception.RecordNotFoundException;

public class WishlistEmailObserverTest {

    // Fake email service che salva le email inviate
    static class FakeEmailService extends EmailService {
        List<String> sentTo = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        List<String> bodies = new ArrayList<>();

        @Override
        public void send(String to, String subject, String body) {
            sentTo.add(to);
            subjects.add(subject);
            bodies.add(body);
        }
    }

    // Mock DAO che restituisce utenti finti
    static class MockWishlistDAO implements WishlistDAO {
        @Override
        public List<String> getUsersWithBookInWishlist(int bookId) throws DAOException {
            return List.of("test1@example.com", "test2@example.com");
        }

		@Override
		public void addToWishlist(String userEmail, int bookId) throws DAOException {
			// do nothing
		}

		@Override
		public void removeFromWishlist(String userEmail, int bookId) throws DAOException {
			// do nothing
		}

		@Override
		public boolean isInWishlist(String userEmail, int bookId) throws DAOException {
			// do nothing
			return true;
		}

		@Override
		public List<Wishlist> getWishlistByUser(String userEmail) throws DAOException, RecordNotFoundException {
			// do nothing
			return null;
		}
    }

    @Test
    void testEmailObserverSendsEmails() {
        FakeEmailService fakeEmailService = new FakeEmailService();
        MockWishlistDAO mockDAO = new MockWishlistDAO();

        // NOTA: serve un costruttore che accetti DAO + EmailService
        WishlistEmailObserver observer = new WishlistEmailObserver(mockDAO, fakeEmailService);

        Book book = new Book();
        book.setId(123);
        book.setTitle("Il Signore degli Anelli");
        book.setAuthor("J.R.R. Tolkien");

        observer.onBookAvailable(book);

        // Verifica: devono essere inviate due mail
        assertEquals(2, fakeEmailService.sentTo.size());
        assertTrue(fakeEmailService.sentTo.contains("test1@example.com"));
        assertTrue(fakeEmailService.sentTo.contains("test2@example.com"));

        // Verifica il subject
        assertTrue(fakeEmailService.subjects.get(0).contains("Buone notizie da Bibliotech"));

        // Verifica corpo mail
        String body = fakeEmailService.bodies.get(0);
        assertTrue(body.contains("Il Signore degli Anelli"));
        assertTrue(body.contains("J.R.R. Tolkien"));
    }
}