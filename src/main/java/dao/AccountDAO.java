package dao;

import exception.RecordNotFoundException;
import exception.DuplicateRecordException;
import model.Account;

import java.sql.SQLException;

public interface AccountDAO {
    Account login(String email, String password) throws SQLException, RecordNotFoundException;
    boolean register(String email, String password, String firstName, String lastName) throws SQLException, DuplicateRecordException;
}