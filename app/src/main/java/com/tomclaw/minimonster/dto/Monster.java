package com.tomclaw.minimonster.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solkin on 13.05.15.
 */
public class Monster {

    private String name;
    private String url;
    private String password;

    private PortsList portsList = new PortsList();

    public Monster() {
    }

    public Monster(String name, String url, String password) {
        this.name = name;
        this.url = url;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessibleUrl() {
        return getUrl() + "/" + getPassword() + "/";
    }

    public PortsList getPortsList() {
        return portsList;
    }

    public void setPortsList(PortsList portsList) {
        this.portsList = portsList;
    }
}
