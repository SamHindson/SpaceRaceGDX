package com.semdog.spacerace.net.entities;

import com.semdog.spacerace.players.Team;

public class VirtualPlayer {
    private String name;
    private int id;
    private Team team;
    private float x, y, dx, dy;
    private boolean alive = true;
    private int score = 0;

    //  Default no-parameter constructor for Kryo deserialization.
    public VirtualPlayer() {
    }

    public VirtualPlayer(String name, int id, Team team, float x, float y) {
        this.name = name;
        this.id = id;
        this.team = team;
        this.x = x;
        this.y = y;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public void addPoint() {
        score++;
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

    public void setID(int id) {
        this.id = id;
    }

    public void setPosition(float x2, float y2) {
        this.x = x2;
        this.y = y2;
    }

    public int getScore() {
        return score;
    }

    public void subtractPoint() {
        score--;
    }
}
