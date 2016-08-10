package com.semdog.spacerace.net.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.universe.Universe;

import java.util.ArrayList;

/**
 * A class which resides on the server holding information about a mass.
 * Acts like a real mass (i.e. it moves, is pulled by gravity, etc.)
 *
 * @author Sam
 */

public class VirtualMass {
    private float x, y, dx, dy, w, h;
    private float angle;

    private Rectangle bounds;
    private VirtualPlanet environment;

    public VirtualMass(float x, float y, float dx, float dy, float w, float h) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.w = w;
        this.h = h;
        bounds = new Rectangle(x, y, w, h);
    }

    public void update(float dt, ArrayList<VirtualPlanet> planets) {
        if (environment != null) angle = MathUtils.atan2(y - environment.getY(), x - environment.getX());

        boolean foundPlanet = false;
        for (VirtualPlanet planet : planets) {
            if (inRange(planet)) {
                foundPlanet = true;

                boolean onGround = Vector2.dst(x, y, planet.getX(), planet.getY()) < planet.getRadius();

                if (!onGround) {
                    if (planet != environment) {
                        setEnvironment(planet);
                    }

                    float force = (float) (Universe.GRAVITY * planet.getMass() / Math.pow(Vector2.dst(x, y, planet.getX(), planet.getY()), 2));
                    float ax = -force * MathUtils.cos(angle);
                    float ay = -force * MathUtils.sin(angle);

                    dx += ax * dt;
                    dy += ay * dt;
                    break;
                } else {
                    dx = dy = 0;
                }
            }
        }

        if (!foundPlanet) {
            environment = null;
        }

        x += dx * dt;
        y += dy * dt;

        bounds.setPosition(x - w / 2, y - h / 2);
        x += dx * dt;
        y += dy * dt;

    }

    private boolean inRange(VirtualPlanet planet) {
        return Vector2.dst(x, y, planet.getX(), planet.getY()) < planet.getSOI();
    }

    public void setEnvironment(VirtualPlanet environment) {
        this.environment = environment;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setId(int id) {
        int id1 = id;
    }

    public void setType(int type) {
        int type1 = type;
    }
}
