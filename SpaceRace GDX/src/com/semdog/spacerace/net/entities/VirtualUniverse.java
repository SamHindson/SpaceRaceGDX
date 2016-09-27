package com.semdog.spacerace.net.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.SpaceRaceServer;
import com.semdog.spacerace.net.UniverseState;
import com.semdog.spacerace.players.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.semdog.spacerace.net.entities.VirtualMass.ROCKET;

/**
 * A class whose object exists on the server side, which keeps track of all the major entities that exist in
 * the multiplayer universe.
 *
 * @author Sam
 */

public class VirtualUniverse {
    public static VirtualUniverse currentUniverse;

    private HashMap<Integer, VirtualMass> masses;
    private HashMap<Integer, MassState> massStates;

    private HashMap<Integer, VirtualPlayer> players;
    private ArrayList<VirtualPlanet> planets;

    private SpaceRaceServer server;

    public VirtualUniverse(SpaceRaceServer server) {
        currentUniverse = this;
        players = new HashMap<>();
        planets = new ArrayList<>();

        masses = new HashMap<>();
        massStates = new HashMap<>();
        this.server = server;
    }

    /**
     * This will handle mass physics, which will run on the server side of things.
     */
    public void update(float dt) {
        for (Map.Entry<Integer, VirtualMass> massEntry : masses.entrySet()) {
            massEntry.getValue().update(this, dt, planets);
            massStates.get(massEntry.getKey()).set(massEntry.getValue().getX(), massEntry.getValue().getY());
        }

        for (Map.Entry<Integer, VirtualMass> massEntry : masses.entrySet()) {
            for(Map.Entry<Integer, VirtualPlayer> playerEntry : players.entrySet()) {
                VirtualMass mass = massEntry.getValue();
                VirtualPlayer player = playerEntry.getValue();
                System.out.println(Vector2.dst(mass.getX(), mass.getY(), player.getX(), player.getY()));
                if(Vector2.dst(mass.getX(), mass.getY(), player.getX(), player.getY()) < 20) {
                    MassEvent massEvent = new MassEvent(MassEvent.PLANET_COLLIDE, massEntry.getKey(), 0);
                    server.sendMassEvent(massEvent);
                    if(mass.getType() == ROCKET)
                        killMass(massEntry.getKey());
                }
            }
        }

        for(Map.Entry<Integer, VirtualMass> massEntry : masses.entrySet()) {
            if(!massEntry.getValue().alive) {
                server.killMass(massEntry.getKey());
                masses.remove(massEntry.getKey());
                return;
            }
        }
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

    /**
     * Adds a planet to the universe
     */
    public void addPlanet(String id, float x, float y, float radius, Color color) {
        planets.add(new VirtualPlanet(id, x, y, radius, color));
    }

    /**
     * Adds a player to the universe
     */
    public void addPlayer(int id, VirtualPlayer player) {
        System.out.println("Getting for " + id);
        players.put(id, player);
    }

    /**
     * Sets a specific player's position
     */
    public void setPlayerPosition(int id, float x, float y) {
        if(players.get(id) != null)  players.get(id).setPosition(x, y);
    }

    /**
     * Gets rid of a particular player
     */
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

    /**
     * Creates a mass from a MassSpawnRequest template
     */
    public void addMass(MassSpawnRequest massSpawnRequest) {
        VirtualMass mass = new VirtualMass(massSpawnRequest.x, massSpawnRequest.y, massSpawnRequest.dx, massSpawnRequest.dy, massSpawnRequest.w, massSpawnRequest.h, massSpawnRequest.lifespan);
        mass.setId(massSpawnRequest.id);
        mass.setType(massSpawnRequest.type);
        massStates.put(massSpawnRequest.id, new MassState());
        masses.put(massSpawnRequest.id, mass);
    }

    /**
     * Returns a nicely-packaged set of data detailing positions of all in-game masses.
     */
    public MassMap getMassStates() {
        return new MassMap(massStates);
    }

    /**
     * Gets rid of a mass
     */
    public void killMass(int id) {
        if(masses.get(id) == null) return;
        masses.get(id).alive = false;
    }

    public HashMap<Integer, VirtualPlayer> getPlayers() {
        for(Map.Entry<Integer, VirtualPlayer> playerEntry : players.entrySet()) {
            System.out.println(playerEntry.getValue().getId() + " ... " + playerEntry.getValue().getTeam());
        }
        return players;
    }

    public ArrayList<VirtualPlanet> getPlanets() {
        return planets;
    }

    public void massHitGround(int id) {
        MassEvent massEvent = new MassEvent(MassEvent.PLANET_COLLIDE, id, 0);
        server.sendMassEvent(massEvent);
    }

    public void setTeam(int id, int i) {
        System.out.println("Setting player " + id + " to team " + (i == 0 ? Team.PINK : Team.BLUE));
        System.out.println(players.get(id).getTeam());
        if(players.get(id) != null) {
            players.get(id).setTeam(i == 0 ? Team.PINK : Team.BLUE);
            System.out.println(players.get(id).getTeam());
        } else {
            System.err.println("That person " + id + " does not exist...");
        }
    }
}
