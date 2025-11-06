package controller.app;

import app.Session;
import app.state.StateManager;
import dao.AccountDAO;
import model.Account;

public class LoginController {

    private final StateManager stateManager;

    public LoginController(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public Account login(String email, String password) throws Exception {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email e password non possono essere vuoti.");
        }

        AccountDAO accountDAO = stateManager.getDaoFactory().getAccountDAO();
        Account account = accountDAO.login(email, password);

        if (account != null) {
            // Salva nella sessione globale
            Session.getInstance().login(account);
        }

        return account;
    }

    public void logout() {
        Session.getInstance().logout();
    }
}