package Assignment.src.model;

public abstract class User {
    private String userID;
    private String name;
    private String password;
    private String email;
    private boolean loggedIn = false;

    //constructor
    public User(String userID, String name, String password, String email){
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    // accessors
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail(){ return email; }
    protected String getPassword() { return password; } // protected in case subclasses need it
    public boolean isLoggedIn() { return loggedIn; }

    // Control login state and password verification
    public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
    public boolean verifyPassword(String pw) { return pw != null && pw.equals(this.password); }
    
    public void changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            throw new IllegalArgumentException("Incorrect old password.");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty.");
        }
        this.password = newPassword;
    }

    // abstract methods to be implemented by subclasses
    public abstract void displayProfile();
    public abstract String getUserType();
}
