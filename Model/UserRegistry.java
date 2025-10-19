package Assignment;

import java.util.HashMap;
import java.util.Map;

public class UserRegistry {
    private final Map<String,User> users = new HashMap<>();

    public boolean register(User user){
        if(user == null || user.getUserID() == null) return false;
        if (users.containsKey(user.getUserID())) return false;
        users.put(user.getUserID(),user);
        return true;
    }

    // find by ID
    public User findById(String userID){
        if (userID == null) return null;
        return users.get(userID);
    }

    // public boolean exists

    public boolean exists(String userID){
        if (userID == null) return false;
        return users.containsKey(userID);
    }
}

