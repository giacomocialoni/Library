package model;

public class User extends Account {
    public User(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName, "logged_user");
    }
    
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
}