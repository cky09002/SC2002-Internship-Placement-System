package view;

/**
 * Represents a menu option with a description and callback action.
 * Used throughout view classes for menu construction.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class MenuOption {
    /** Description of the menu option */
    private final String desc;
    
    /** Callback action to execute when selected */
    private final Runnable onSelCallback;

    /**
     * Constructs a MenuOption with description and callback.
     * 
     * @param desc The menu option description
     * @param onSelCallback The action to perform when selected
     */
    public MenuOption(String desc, Runnable onSelCallback) {
        this.desc = desc;
        this.onSelCallback = onSelCallback;
    }

    /**
     * Gets the menu option description.
     * @return the menu option description
     */
    public String getDesc() { return desc; }
    
    /**
     * Gets the callback action.
     * @return the callback action
     */
    public Runnable getOnSelCallback() { return onSelCallback; }
}