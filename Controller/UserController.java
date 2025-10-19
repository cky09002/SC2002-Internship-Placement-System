package Assignment.Controller;

import Assignment.Model.UserRegistry;

public abstract class UserController {
    protected final UserRegistry registry;

    // constructor – inject the registry dependency
    protected UserController(UserRegistry registry) {
        if (registry == null) throw new IllegalArgumentException("registry cannot be null");
        this.registry = registry;
    }

}
