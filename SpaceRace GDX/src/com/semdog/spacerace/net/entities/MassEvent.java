package com.semdog.spacerace.net.entities;

/**
 * Created by sam on 2016/09/27.
 */
public class MassEvent {
    public static final int PLANET_COLLIDE = 0;
    public static final int PLAYER_COLLIDE = 1;
    public static final int MASS_COLLIDE = 2;
    public static final int OLD_AGE = 3;

    int massID;
    int event;
    float[] data;

    public MassEvent() {
    }

    public MassEvent(int event, int massID, float... data) {
        this.event = event;
        this.massID = massID;
        this.data = data;
    }

    public int getEvent() {
        return event;
    }

    public int getMassID() {
        return massID;
    }

    public float[] getData() {
        return data;
    }
}
