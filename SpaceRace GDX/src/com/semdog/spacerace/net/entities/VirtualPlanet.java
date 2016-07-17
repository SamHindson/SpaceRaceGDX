package com.semdog.spacerace.net.entities;

import com.badlogic.gdx.math.Vector2;

public class VirtualPlanet {
    private float x, y, radius;
    private String id;

    public VirtualPlanet() {

    }

    public VirtualPlanet(String id, float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public String getID() {
        return id;
    }

    public float getSOI() {
        return radius * 3;
    }

    public float getMass() {
        return 5 * radius * radius;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }
}
