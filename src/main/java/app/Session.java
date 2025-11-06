package app;

import model.Account;

public class Session {

    private static Session instance;
    private Account loggedUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null)
            instance = new Session();
        return instance;
    }

    public void login(Account account) {
        this.loggedUser = account;
    }

    public void logout() {
        this.loggedUser = null;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public Account getLoggedUser() {
        return loggedUser;
    }
}