package com.semdog.spacerace.net.entities;

/**
 * A class which is sent to the server to request the creation of a bullet.
 *
 * @author Sam
 */

public class BulletRequest {
    private float x, y, dx, dy;
    private int damage;
    private int ownerID;

    //  Default no-parameter constructor for Kryo deserialization.
    public BulletRequest() {
    }

    public BulletRequest(int ownerID, float x, float y, float dx, float dy, int damage) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.damage = damage;
        this.ownerID = ownerID;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public int getDamage() {
        return damage;
    }

    public int getOwnerID() {
        return ownerID;
    }
}
