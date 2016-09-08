package com.semdog.spacerace.net.entities;

/**
 * A class which is sent to the server to request the destruction of a mass.
 *
 * @author Sam
 */

public class MassKillRequest {
    private int id;

    //  Default no-parameter constructor for Kryo deserialization.
    MassKillRequest() {
    }

    public MassKillRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
