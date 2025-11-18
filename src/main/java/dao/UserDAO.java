package dao;

import java.util.List;

import model.User;

public interface UserDAO {
	User getUserByEmail(String email);
	List<User> getAllUsers();
}
