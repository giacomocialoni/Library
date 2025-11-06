package model;

public abstract class Account {
    protected String email;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String role; // "admin" o "logged_user"

    public Account(String email, String password, String firstName, String lastName, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + role + ")";
    }
}