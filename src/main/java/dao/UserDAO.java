package dao;

import model.User;
import exception.DAOException;
import java.util.List;

public interface UserDAO {
    User getUserByEmail(String email) throws DAOException;
    List<User> getAllUsers() throws DAOException;
    List<User> getLoggedUsers() throws DAOException;
    List<User> searchUsers(String searchTerm) throws DAOException;
    void deleteUser(String email) throws DAOException;
}