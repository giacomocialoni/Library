package controller.app;

import app.Session;
import bean.AccountBean;
import dao.AccountDAO;
import dao.factory.DAOFactory;
import exception.DAOException;
import exception.DuplicateRecordException;
import exception.IncorrectDataException;
import model.Account;
import model.User;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignInController {

    private static final Logger logger = LoggerFactory.getLogger(SignInController.class);

    private final AccountDAO accountDAO;

    public SignInController() {
        this.accountDAO = DAOFactory.getActiveFactory().getAccountDAO();
    }

    public AccountBean signIn(String email, String password, String firstName, String lastName) throws DAOException {
        if (email == null || email.isBlank() || password == null || password.isBlank() ||
            firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Tutti i campi sono obbligatori.");
        }

        boolean success;
		try {
			success = accountDAO.register(email, password, firstName, lastName);
		} catch (SQLException | DuplicateRecordException e) {
            logger.warn("SignIn fallito per {}", email, e);
            return null;
		}

        if (!success) {
            throw new DAOException("Email già esistente!");
        }

        Account account = new User(email, password, firstName, lastName);
        Session.getInstance().login(account);

        try {
            AccountBean bean = new AccountBean();
            bean.setEmail(account.getEmail());
            bean.setFirstName(account.getFirstName());
            bean.setLastName(account.getLastName());
            bean.setRole(account.getRole());
            return bean;
        } catch (IncorrectDataException e) {
            logger.warn("Errore dati account durante la registrazione: {}", email, e);
            return null;
        }
    }
}