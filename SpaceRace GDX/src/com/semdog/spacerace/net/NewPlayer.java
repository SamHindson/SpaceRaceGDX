package com.semdog.spacerace.net;

import com.semdog.spacerace.net.entities.VirtualPlayer;

class NewPlayer extends NetworkEvent {

    private VirtualPlayer player;
    private String name;
    private int team;
    private int id;

    public NewPlayer() {

    }

    public NewPlayer(VirtualPlayer player) {
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
