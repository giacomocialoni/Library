package dao;

import model.User;
import exception.DAOException;
import exception.RecordNotFoundException;
import java.util.List;

public interface UserDAO {
    User getUserByEmail(String email) throws DAOException, RecordNotFoundException;
    List<User> getAllUsers() throws DAOException, RecordNotFoundException;
    List<User> getLoggedUsers() throws DAOException, RecordNotFoundException;
    List<User> searchUsers(String searchTerm) throws DAOException, RecordNotFoundException;
    void deleteUser(String email) throws DAOException, RecordNotFoundException;
}