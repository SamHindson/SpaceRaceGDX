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
    public static final int GRENADE = 0;
    public static final int ROCKET = 1;

    private float x, y, dx, dy, w, h;
    private float angle;
    private float lifespan, age;

    private int id;
    private boolean onGround;

    private int type = -1;

    private Rectangle bounds;
    private VirtualPlanet environment;

    public VirtualMass(float x, float y, float dx, float dy, float w, float h, float lifespan) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.w = w;
        this.h = h;
        this.lifespan = lifespan;
        bounds = new Rectangle(x, y, w, h);
    }

    public void update(VirtualUniverse virtualUniverse, float dt, ArrayList<VirtualPlanet> planets) {
        age += dt;
        if (age > lifespan) {
            virtualUniverse.killMass(id);
            return;
        }
        if (environment != null) angle = MathUtils.atan2(y - environment.getY(), x - environment.getX());

        boolean foundPlanet = false;
        for (VirtualPlanet planet : planets) {
            if (inRange(planet)) {
                foundPlanet = true;

                if (!onGround) {
                    if (Vector2.dst(x, y, planet.getX(), planet.getY()) < planet.getRadius()) {
                        onGround = true;
                        if (type == ROCKET) {
                            virtualUniverse.killMass(id);
                        } else {
                            virtualUniverse.massHitGround(id);
                        }
                        break;
                    }
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
                    if (Vector2.dst(x, y, planet.getX(), planet.getY()) > planet.getRadius()) onGround = false;
                    dx = dy = 0;
                    x = planet.getX() + (planet.getRadius() + 2) * MathUtils.cos(angle);
                    y = planet.getY() + (planet.getRadius() + 2) * MathUtils.sin(angle);
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
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }
}
