package com.tomclaw.minimonster.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor on 13.05.2015.
 */
public class PortsList {

    private List<Port> ports = new ArrayList<>();

    public PortsList() {
    }

    public void addPort(Port port) {
        ports.add(port);
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    public void merge(PortsList portsList) {
    }
}
