package dao;

import model.Account;

public interface AccountDAO {
    Account login(String email, String password) throws Exception;
}