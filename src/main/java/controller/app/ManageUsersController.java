package controller.app;

import dao.UserDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.RecordNotFoundException;
import model.User;
import bean.UserBean;
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

    public List<UserBean> searchUsers(String searchText) {
        try {
            List<User> allUsers = userDAO.getLoggedUsers();
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lower = searchText.toLowerCase();
                allUsers = allUsers.stream()
                        .filter(u -> u.getEmail().toLowerCase().contains(lower)
                                || u.getFirstName().toLowerCase().contains(lower)
                                || u.getLastName().toLowerCase().contains(lower))
                        .collect(Collectors.toList());
            }
            return mapUsersToBeans(allUsers);
        } catch (DAOException e) {
            logger.error("Errore DAO durante la ricerca utenti", e);
            return List.of();
        }
    }

    public List<UserBean> searchUsersByEmail(String searchText) {
        try {
            List<User> allUsers = userDAO.getAllUsers();
            allUsers = allUsers.stream()
                    .filter(u -> u.getEmail().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
            return mapUsersToBeans(allUsers);
        } catch (DAOException e) {
            logger.error("Errore DAO ricerca utenti per email", e);
            return List.of();
        }
    }

    public List<UserBean> searchUsersByName(String searchText) {
        try {
            List<User> allUsers = userDAO.getAllUsers();
            allUsers = allUsers.stream()
                    .filter(u -> u.getFirstName().toLowerCase().contains(searchText.toLowerCase())
                              || u.getLastName().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
            return mapUsersToBeans(allUsers);
        } catch (DAOException e) {
            logger.error("Errore DAO ricerca utenti per nome", e);
            return List.of();
        }
    }

    // ===== CRUD UTENTI =====

    public List<UserBean> getAllUsers() {
        try {
            return mapUsersToBeans(userDAO.getAllUsers());
        } catch (RecordNotFoundException e) {
            logger.info("Nessun utente trovato");
            return List.of();
        } catch (DAOException e) {
            logger.error("Errore DAO durante il recupero di tutti gli utenti", e);
            return List.of();
        }
    }

    public List<UserBean> getLoggedUsers() {
        try {
            return mapUsersToBeans(userDAO.getLoggedUsers());
        } catch (DAOException e) {
            logger.error("Errore DAO recupero utenti loggati", e);
            return List.of();
        }
    }

    public UserBean getUserByEmail(String email) {
        try {
            User user = userDAO.getUserByEmail(email);
            return mapUserToBean(user);
        } catch (RecordNotFoundException e) {
            logger.warn("Utente non trovato: {}", email, e);
            return null;
        } catch (DAOException e) {
            logger.error("Errore DAO recupero utente: {}", email, e);
            return null;
        }
    }

    public boolean deleteUser(String email) {
        try {
            userDAO.deleteUser(email);
            return true;
        } catch (RecordNotFoundException e) {
            logger.warn("Utente da eliminare non trovato: {}", email, e);
            return false;
        } catch (DAOException e) {
            logger.error("Errore DAO durante la cancellazione dell'utente: {}", email, e);
            return false;
        }
    }

    // ===== METODI UTILI =====

    public int getTotalUsersCount() {
        return getLoggedUsers().size();
    }

    public int getRegularUsersCount() {
        try {
            return (int) userDAO.getLoggedUsers().stream().count();
        } catch (DAOException e) {
            logger.error("Errore conteggio utenti normali", e);
            return 0;
        }
    }

    public int getAdminUsersCount() {
        try {
            return (int) userDAO.getAllUsers().stream()
                    .filter(u -> u.getRole().equalsIgnoreCase("admin"))
                    .count();
        } catch (DAOException e) {
            logger.error("Errore conteggio utenti admin", e);
            return 0;
        }
    }

    // ===== MAPPING User → UserBean =====

    private List<UserBean> mapUsersToBeans(List<User> users) {
        return users.stream()
                .map(this::mapUserToBean)
                .collect(Collectors.toList());
    }

    private UserBean mapUserToBean(User user) {
        UserBean bean = new UserBean();
        bean.setEmail(user.getEmail());
        bean.setFirstName(user.getFirstName());
        bean.setLastName(user.getLastName());
        bean.setPassword(user.getPassword());
        return bean;
    }
}