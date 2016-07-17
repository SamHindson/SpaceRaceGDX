package com.semdog.spacerace.players;

import java.util.HashMap;

/**
 * A little class to hold all of the player's necessary HUD information
 *
 * @author Sam
 */

public class VitalSigns {
    private HashMap<String, Vitality> signs;

    public VitalSigns() {
        signs = new HashMap<>();
    }

    public void addItems(Vitality... vitalities) {
        for (Vitality vitality : vitalities) {
            signs.put(vitality.getID(), vitality);
        }
    }

    public void removeItems(String... ids) {
        for (String id : ids) {
            signs.remove(id);
        }
    }

    public Vitality get(String id) {
        return signs.get(id);
    }

    public HashMap<String, Vitality> getSigns() {
        return signs;
    }

    public boolean has(String string) {
        return signs.containsKey(string);
    }

    public enum Type {
        DISCRETE, CONTINUOUS
    }
}
