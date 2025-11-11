package dao;

import java.sql.SQLException;

import model.Account;

public interface AccountDAO {
    Account login(String email, String password) throws Exception;
    boolean register(String email, String password, String firstName, String lastName) throws SQLException;
}