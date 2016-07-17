package com.semdog.spacerace.net;

/**
 * Created by Sam on 2016/07/17.
 */
public class MassRequest {
    private int requesterID;
    private float mass, x, y;

    public MassRequest() {
    }

    public MassRequest(int requesterID, float x, float y, float mass) {
        this.requesterID = requesterID;
        this.x = x;
        this.y = y;
        this.mass = mass;
    }

    public int getRequesterID() {
        return requesterID;
    }

    public float getMass() {
        return mass;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
