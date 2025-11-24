package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructorAndGetters() {
        User user = new User("user@example.com", "pass123", "Luigi", "Bianchi");

        assertEquals("user@example.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("Luigi", user.getFirstName());
        assertEquals("Bianchi", user.getLastName());
        assertEquals("logged_user", user.getRole());
    }

    @Test
    void testIsAdmin() {
        User user = new User("user@example.com", "pass123", "Luigi", "Bianchi");
        assertFalse(user.isAdmin()); // deve ritornare false perché non è admin
    }
}