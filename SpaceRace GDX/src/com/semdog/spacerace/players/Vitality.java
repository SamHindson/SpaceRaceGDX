package com.semdog.spacerace.players;

/**
 * Created by Sam on 07-May-16.
 * <p>
 * An interface which all things vital-sign related must implement
 */
public interface Vitality {
    float getValue();

    float getMaxValue();

    VitalSigns.Type getType();
}
