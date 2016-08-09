package com.semdog.spacerace.net.entities;

/**
 * Created by Sam on 2016/08/08.
 */

public class MassState {
    float x, y;

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
