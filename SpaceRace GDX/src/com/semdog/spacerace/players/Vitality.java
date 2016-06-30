package com.semdog.spacerace.players;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Sam on 07-May-16.
 * <p>
 * An interface which all things vital-sign related must implement
 */
public interface Vitality {
	String getID();
    float getValue();
    float getMaxValue();
    Color getColor();
    VitalSigns.Type getValueType();
}
