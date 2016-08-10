package com.semdog.spacerace.net.entities;

/**
 * A class which is sent to the server to request the spawning of a mass.
 * Currently only supports Grenades
 *
 * @author Sam
 */

public class MassSpawnRequest {
    public static final int GRENADE = 0;
    int id;
    int type;
    float x, y, dx, dy;
    float w, h;

    //  Default no-parameter constructor for Kryo deserialization.
    MassSpawnRequest() {
    }

    public MassSpawnRequest(int type, float x, float y, float dx, float dy) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
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
}
