package controller.app;

import dao.UserDAO;
import dao.factory.DAOFactory;
import model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ManageUsersController {

    private final UserDAO userDAO;

    public ManageUsersController() {
        this.userDAO = DAOFactory.getActiveFactory().getUserDAO();
    }

    // ===== RICERCHE UTENTI =====

    public List<User> searchUsers(String searchText) {
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
    }

    public List<User> searchUsersByEmail(String searchText) {
        return userDAO.getAllUsers().stream()
                .filter(user -> user.getEmail().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<User> searchUsersByName(String searchText) {
        return userDAO.getAllUsers().stream()
                .filter(user -> 
                        user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                        user.getLastName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ===== OPERAZIONI CRUD =====

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    public boolean deleteUser(String email) {
        try {
            if (userDAO instanceof dao.database.DatabaseUserDAO) {
                return ((dao.database.DatabaseUserDAO) userDAO).deleteUser(email);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // ===== METODI UTILI =====

    public int getTotalUsersCount() {
        return userDAO.getAllUsers().size();
    }

    public int getAdminUsersCount() {
        return (int) userDAO.getAllUsers().stream()
                .filter(user -> user.isAdmin())
                .count();
    }

    public int getRegularUsersCount() {
        return (int) userDAO.getAllUsers().stream()
                .filter(user -> !user.isAdmin())
                .count();
    }

    // ===== METODI PER ATTIVITÀ UTENTE =====

    public String getUserActivitySummary(String userEmail) {
        // Questo metodo può essere usato per informazioni aggiuntive
        return "Attività utente: " + userEmail;
    }
}