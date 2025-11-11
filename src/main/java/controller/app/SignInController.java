package controller.app;

import app.Session;
import app.state.StateManager;
import dao.AccountDAO;
import model.Account;
import model.User;

public class SignInController {

    private final StateManager stateManager;
    
    public SignInController(StateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    public Account signIn(String email, String password, String firstName, String lastName) throws Exception {
        if (email == null || email.isBlank() || password == null || password.isBlank() ||
            firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Tutti i campi sono obbligatori.");
        }

        AccountDAO accountDAO = stateManager.getDaoFactory().getAccountDAO();
        boolean success = accountDAO.register(email, password, firstName, lastName);

        if (success) {
            // Se la registrazione ha successo, effettua il login automatico
            Account account = new User(email, password, firstName, lastName);
            Session.getInstance().login(account);
            return account;
        } else {
            throw new Exception("Email già esistente!");
        }
    }
}