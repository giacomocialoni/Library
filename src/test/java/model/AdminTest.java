package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void testAdminConstructorAndGetters() {
        Admin admin = new Admin("admin@example.com", "password123", "Mario", "Rossi");

        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("password123", admin.getPassword());
        assertEquals("Mario", admin.getFirstName());
        assertEquals("Rossi", admin.getLastName());
        assertEquals("admin", admin.getRole());
    }

    @Test
    void testIsAdmin() {
        Admin admin = new Admin("admin@example.com", "password123", "Mario", "Rossi");
        assertTrue(admin.isAdmin());
    }
}