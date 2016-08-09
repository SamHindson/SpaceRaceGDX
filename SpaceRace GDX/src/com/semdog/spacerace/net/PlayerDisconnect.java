package com.semdog.spacerace.net;

/**
 * A class sent to the server when a player quits.
 * Stores the player in question's connection ID.
 */

class PlayerDisconnect {
    private int id;

    //  Default no-parameter constructor for Kryo deserialization.
    public PlayerDisconnect() {
    }

    PlayerDisconnect(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
