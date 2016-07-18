package com.semdog.spacerace.net.entities;

import com.semdog.spacerace.net.UniverseState;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class whose object exists on the server side, which keeps track of all the major entities that exist in the multiplayer universe.
 */

public class VirtualUniverse {
    public static VirtualUniverse currentUniverse;

    private HashMap<Integer, VirtualPlayer> players;
    private ArrayList<VirtualPlanet> planets;

    public VirtualUniverse() {
        currentUniverse = this;
        players = new HashMap<>();
        planets = new ArrayList<>();
    }

    /**
     * This will handle mass physics, which will run on the server side of things.
     */
    public void update(float dt) {

    }

    /**
     * Handles when a player kills another player.
     * A point should be added if the killer killed an enemy, and subtracted if it was a treacherous kill.
     */
    public void killPoint(int victim, int killer) {
        if (players.get(victim).getTeam() == players.get(killer).getTeam())
            players.get(killer).subtractPoint();
        else
            players.get(killer).addPoint();
    }

    /** Adds a planet to the universe */
    public void addPlanet(String id, float x, float y, float radius) {
        planets.add(new VirtualPlanet(id, x, y, radius));
    }

    /** Adds a player to the universe */
    public void addPlayer(int id, VirtualPlayer player) {
        players.put(id, player);
    }

    /** Sets a specific player's position */
    public void setPlayerPosition(int id, float x, float y) {
        players.get(id).setPosition(x, y);
    }

    /** Gets rid of a particular player */
    public void removePlayer(int id) {
        players.remove(id);
    }

    /**
     * Returns the universe's current planets and players packaged nicely into a UniverseState object.
     * Used when a new player joins a server and needs to be updated with information about which players are where and what planets there are.
     */
    public UniverseState getState() {
        UniverseState state = new UniverseState();
        state.setPlanets(planets);
        state.setPlayers(players);
        return state;
    }

    /**
     * Sets the life properties of a particular player (i.e. dead or alive)
     */
    public void setLifeInfo(int id, float v) {
        players.get(id).setAlive(v > 0);
    }

    public HashMap<Integer, VirtualPlayer> getPlayers() {
        return players;
    }

    public ArrayList<VirtualPlanet> getPlanets() {
        return planets;
    }
}
