package com.semdog.spacerace.net.entities;

import com.semdog.spacerace.players.Team;

/**
 * A class utilized by the server to keep track of players.
 * It stores their position, team, name. environment, and name.
 *
 * @author Sam
 */

public class VirtualPlayer {
    private String name;
    private int id;
    private Team team;
    private float x, y, dx, dy;
    private float environmentX, environmentY;
    private boolean alive = true;
    private int score = 0;

    //  Default no-parameter constructor for Kryo deserialization.
    public VirtualPlayer() {
    }

    public VirtualPlayer(String name, int id, Team team, float x, float y, float environmentX, float environmentY) {
        this.name = name;
        this.id = id;
        this.team = team;
        this.x = x;
        this.y = y;
        this.environmentX = environmentX;
        this.environmentY = environmentY;
        System.out.println("Set a virtual player with ID ");
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

    public float getEnvironmentX() {
        return environmentX;
    }

    public float getEnvironmentY() {
        return environmentY;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
