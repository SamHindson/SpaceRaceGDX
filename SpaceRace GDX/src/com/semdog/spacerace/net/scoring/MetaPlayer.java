package com.semdog.spacerace.net.scoring;

import com.semdog.spacerace.players.Team;

/**
 * A class used on the client side to keep track of the points of the other players.
 * The methods here are all self-explanatory.
 *
 * @author Sam
 */

public class MetaPlayer {
    private int score;
    private int id;
    private Team team;
    private String name;

    public MetaPlayer() {

    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(int team2) {
        team = team2 == 1 ? Team.PINK : Team.BLUE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addPoint() {
        score++;
    }

    public void subtractPoint() {
        score--;
    }
}
