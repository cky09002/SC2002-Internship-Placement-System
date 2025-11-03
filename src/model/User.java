package Assignment.src.model;

import Assignment.src.utils.ValidationHelper;

public abstract class User {
    private String userID;
    private String name;
    private String password;
    private String email;
    private boolean loggedIn = false;

    public User(String userID, String name, String password, String email){
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail(){ return email; }
    protected String getPassword() { return password; }
    public boolean isLoggedIn() { return loggedIn; }
    
    public void setName(String name) {
        ValidationHelper.validateNotEmpty(name, "Name");
        this.name = name;
    }
    
    public void setEmail(String email) {
        ValidationHelper.validateEmail(email);
        this.email = email;
    }

    public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
    public boolean verifyPassword(String pw) { return pw != null && pw.equals(this.password); }
    
    public void logout() {
        setLoggedIn(false);
    }
    
    public void changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            throw new IllegalArgumentException("Incorrect old password.");
        }
        ValidationHelper.validateNotEmpty(newPassword, "New password");
        this.password = newPassword;
    }

    public abstract String getUserType();
}
