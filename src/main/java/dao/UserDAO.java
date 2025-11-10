package dao;

import model.User;

public interface UserDAO {
	User getUserByEmail(String email);
}
