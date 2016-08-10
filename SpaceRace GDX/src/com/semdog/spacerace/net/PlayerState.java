package com.semdog.spacerace.net;

/**
 * A multipurpose class that is used whenever a player needs to convey its state to the server.
 * <p>
 * Rather than having many separate classes for each event (move, animation state, killed, etc.) which would have
 * very similar fields anyway, we will rather have it in one class with an identifier and an array for storing that
 * extra data.
 *
 * @author Sam
 */

public class PlayerState {
    public static final int SETPOS = 0;
    public static final int ANIMSTATE = 1;
    public static final int ENVSTATE = 2;
    public static final int LIFE = 3;
    public static final int BULLETKILL = 4;

    private int id;
    private int category;
    private float[] information;

    // Default no-parameter constructor for Kryo deserialization.
    public PlayerState() {
    }

    public PlayerState(int id, int category, float... information) {
        this.category = category;
        this.information = information;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getCategory() {
        return category;
    }

    public float[] getInformation() {
        return information;
    }

    public void setInformation(float[] information) {
        this.information = information;
    }
}
