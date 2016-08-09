package com.semdog.spacerace.net.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.universe.Universe;

import java.util.ArrayList;

/**
 * Created by Sam on 2016/08/08.
 */
public class VirtualMass {
    int id, type;
    float x, y, dx, dy, w, h;
    float angle;
    boolean gravityEnabled;
    boolean onGround;

    Rectangle bounds;
    VirtualPlanet environment;

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

                onGround = Vector2.dst(x, y, planet.getX(), planet.getY()) < planet.getRadius();

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
}
