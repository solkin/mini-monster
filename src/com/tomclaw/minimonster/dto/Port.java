package com.tomclaw.minimonster.dto;

/**
 * Created by solkin on 13.05.15.
 */
public class Port {

    private String name;
    private int index;
    private boolean value;

    public Port() {
    }

    public Port(String name, int index, boolean value) {
        this.name = name;
        this.index = index;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
