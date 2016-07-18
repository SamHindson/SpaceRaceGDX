package com.semdog.spacerace.players;

import com.badlogic.gdx.graphics.Color;

/**
 * An interface which all things vital-sign related must implement.
 * Things which implement this will appear on the HUD as the little colored bars
 */

public interface Vitality {
    String getID(); // How it will be referenced programatically

    String getDisplayName(); // How it will appear to the user

    float getValue(); // Its current value

    float getMaxValue(); // Its maximum value

    Color getColor(); // Its color

    VitalSigns.Type getValueType(); // Whether it is continuous or discrete
}
