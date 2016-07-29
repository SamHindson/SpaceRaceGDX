package com.semdog.spacerace.net;

/**
 * A class sent over the Kryo server to request the instantiation of a new mass.
 * Not yet implemented. TODO: Implement!
 */

public class MassRequest {
    private int requesterID;
    private float mass, x, y;

    //  Default no-parameter constructor for Kryo deserialization.
    public MassRequest() {
    }

    MassRequest(int requesterID, float x, float y, float mass) {
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
