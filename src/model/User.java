package model;

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

    // setters (accept values)
    public void setUserID(String userID) { this.userID = userID; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email){this.email = email; }

    // package-private helpers for controller (keeps business logic out of model)
    void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
    boolean verifyPassword(String pw) { return pw != null && pw.equals(this.password); }
    void setPasswordInternal(String newPassword) { this.password = newPassword; }

    // abstract methods to be implemented by subclasses
    public abstract void displayProfile();
    public abstract String getUserType();


}
