package com.semdog.spacerace.net.entities;

import com.badlogic.gdx.math.Vector2;

/**
 * A class utilized by the server to keep track of planets.
 * It stores their position, radius and name.
 *
 * @author Sam
 */

public class VirtualPlanet {
    private float x, y, radius;
    private String id;

    //  Default no-parameter constructor for Kryo deserialization.
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
