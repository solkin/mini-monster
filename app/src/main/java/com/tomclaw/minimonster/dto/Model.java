package com.tomclaw.minimonster.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solkin on 13.05.15.
 */
public class Model {

    private List<Monster> monsters = new ArrayList<>();
    private int active = 0;

    public Model() {
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters = monsters;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public boolean isEmpty() {
        return monsters == null || monsters.isEmpty();
    }

    public Monster getActiveMonster() {
        return getMonsters().get(getActive());
    }
}
