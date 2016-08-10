package com.semdog.spacerace.net.entities;

/**
 * A class which wraps information about a mass
 *
 * @author Sam
 */

public class MassState {
    private float x, y;

    //  Default no-parameter constructor for Kryo deserialization.
    MassState() {
    }

    void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
