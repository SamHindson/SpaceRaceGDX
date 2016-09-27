package com.semdog.spacerace.net.entities;

/**
 * A class which is sent to the server to request the spawning of a mass.
 * Currently only supports Grenades
 *
 * @author Sam
 */

public class MassSpawnRequest {
    public static final int GRENADE = 0;
    public static final int ROCKET = 1;
    int id;
    int type = -1;
    int owner;
    float x, y, dx, dy;
    float w, h;
    float lifespan;

    //  Default no-parameter constructor for Kryo deserialization.
    MassSpawnRequest() {
    }

    public MassSpawnRequest(int type, float x, float y, float dx, float dy, float lifespan) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.lifespan = lifespan;
        id = hashCode();
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getW() {
        return w;
    }

    public float getH() {
        return h;
    }

    public float getDX() {
        return dx;
    }

    public float getDY() {
        return dy;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }
}
