package com.sc2002.assignment.view;

public class MenuOption {
    private final String desc;
    private final Runnable onSelCallback;

    public MenuOption(String desc, Runnable onSelCallback) {
        this.desc = desc;
        this.onSelCallback = onSelCallback;
    }

    public String getDesc() { return desc; }
    public Runnable getOnSelCallback() { return onSelCallback; }
}