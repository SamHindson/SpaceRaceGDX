package com.semdog.spacerace.net;

import com.semdog.spacerace.net.entities.VirtualPlayer;

/**
 * A class sent to the server when a new player has joined.
 * Stores all the necessary information for that joining player.
 *
 * @author Sam
 */

public class NewPlayer {
    private VirtualPlayer player;
    private int id;

    //  Default no-parameter constructor for Kryo deserialization.
    public NewPlayer() {
    }

    NewPlayer(VirtualPlayer player) {
        this.player = player;
    }

    public VirtualPlayer getPlayer() {
        return player;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        player.setID(id);
    }
}
