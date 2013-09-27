package com.example.minimonster;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/25/13
 * Time: 7:30 PM
 */
public class Switcher {

    private int switcherPort;
    private boolean switcherValue;

    public Switcher(int switcherPort, boolean switcherValue) {
        this.switcherPort = switcherPort;
        this.switcherValue = switcherValue;
    }

    public int getSwitcherPort() {
        return switcherPort;
    }

    public boolean isSwitcherValue() {
        return switcherValue;
    }
}
