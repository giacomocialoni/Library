package controller.app;

import dao.UserDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ManageUsersController {

    private static final Logger logger = LoggerFactory.getLogger(ManageUsersController.class);

    private final UserDAO userDAO;

    public ManageUsersController() {
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
    }

    // ===== RICERCHE UTENTI =====

    public List<User> searchUsers(String searchText) {
        try {
            List<User> allUsers = userDAO.getAllUsers();
            if (searchText == null || searchText.trim().isEmpty()) {
                return allUsers;
            }

            String finalSearchText = searchText.toLowerCase();
            return allUsers.stream()
                    .filter(user ->
                            user.getEmail().toLowerCase().contains(finalSearchText) ||
                            user.getFirstName().toLowerCase().contains(finalSearchText) ||
                            user.getLastName().toLowerCase().contains(finalSearchText))
                    .collect(Collectors.toList());

        } catch (DAOException e) {
            logger.error("Errore DAO durante la ricerca degli utenti", e);
            return List.of();
        }
    }

    public List<User> searchUsersByEmail(String searchText) {
        try {
            return userDAO.getAllUsers().stream()
                    .filter(user -> user.getEmail().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (DAOException e) {
            logger.error("Errore DAO durante la ricerca utenti per email", e);
            return List.of();
        }
    }

    public List<User> searchUsersByName(String searchText) {
        try {
            return userDAO.getAllUsers().stream()
                    .filter(user ->
                            user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getLastName().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (DAOException e) {
            logger.error("Errore DAO durante la ricerca utenti per nome", e);
            return List.of();
        }
    }

    // ===== OPERAZIONI CRUD =====

    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero di tutti gli utenti", e);
            return List.of();
        }
    }

    public User getUserByEmail(String email) {
        try {
            return userDAO.getUserByEmail(email);
        } catch (RecordNotFoundException e) {
            logger.warn("Utente non trovato: " + email, e);
            return null;
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero utente: " + email, e);
            return null;
        }
    }

    public boolean deleteUser(String email) {
        //TODO Impementa eliminazione utente
    	return false;
    }

    // ===== METODI UTILI =====

    public int getTotalUsersCount() {
        try {
            return userDAO.getAllUsers().size();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il conteggio totale utenti", e);
            return 0;
        }
    }

    public int getAdminUsersCount() {
        try {
            return (int) userDAO.getAllUsers().stream()
                    .filter(User::isAdmin)
                    .count();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il conteggio utenti admin", e);
            return 0;
        }
    }

    public int getRegularUsersCount() {
        try {
            return (int) userDAO.getAllUsers().stream()
                    .filter(user -> !user.isAdmin())
                    .count();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il conteggio utenti normali", e);
            return 0;
        }
    }

    // ===== METODI PER ATTIVITÀ UTENTE =====

    public String getUserActivitySummary(String userEmail) {
        // Metodo placeholder per eventuali informazioni aggiuntive
        return "Attività utente: " + userEmail;
    }
}