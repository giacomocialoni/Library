package bean;

import exception.IncorrectDataException;

public class AdminBean {

    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public AdminBean() {}

    // ================== GETTER & SETTER ==================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws IncorrectDataException {
        if (email == null || email.isBlank())
            throw new IncorrectDataException("Email admin non valida");
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) throws IncorrectDataException {
        if (firstName == null || firstName.isBlank())
            throw new IncorrectDataException("Nome admin non valido");
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) throws IncorrectDataException {
        if (lastName == null || lastName.isBlank())
            throw new IncorrectDataException("Cognome admin non valido");
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) throws IncorrectDataException {
        if (role == null || role.isBlank())
            throw new IncorrectDataException("Ruolo admin non valido");
        this.role = role;
    }
}