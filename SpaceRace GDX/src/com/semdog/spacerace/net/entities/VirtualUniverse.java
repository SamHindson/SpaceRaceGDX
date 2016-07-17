package com.semdog.spacerace.net.entities;

import com.semdog.spacerace.net.UniverseState;
import com.semdog.spacerace.players.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VirtualUniverse {
    public static VirtualUniverse currentUniverse;

    private HashMap<Integer, VirtualPlayer> players;
    private HashMap<Integer, VirtualMass> masses;
    private ArrayList<VirtualPlanet> planets;
    private ArrayList<VirtualPlayer> pinks, blues;

    public VirtualUniverse() {
        currentUniverse = this;
        players = new HashMap<>();
        planets = new ArrayList<>();
        masses = new HashMap<>();
        pinks = new ArrayList<>();
        blues = new ArrayList<>();
    }

    public HashMap<Integer, VirtualPlayer> getPlayers() {
        return players;
    }

    public void addPlanet(String id, float x, float y, float radius) {
        planets.add(new VirtualPlanet(id, x, y, radius));
    }

    public void addPlayer(int id, VirtualPlayer player) {
        players.put(id, player);

        if (player.getTeam() == Team.PINK) pinks.add(player);
        else blues.add(player);
    }

    public void setPlayerPosition(int id, float x, float y) {
        players.get(id).setPosition(x, y);
    }

    public void removePlayer(int id) {
        players.remove(id);
    }

    public UniverseState getState() {
        UniverseState state = new UniverseState();
        state.setPlanets(planets);
        state.setPlayers(players);
        return state;
    }

    public void killPoint(int victim, int killer) {
        if (players.get(victim).getTeam() == players.get(killer).getTeam())
            players.get(killer).subtractPoint();
        else
            players.get(killer).addPoint();
    }

    public void addMass(VirtualMass mass) {
        masses.put(mass.hashCode(), mass);
    }

    public ArrayList<VirtualPlanet> getPlanets() {
        return planets;
    }

    public void setLifeInfo(int id, float v) {
        players.get(id).setAlive(v > 0);
    }

    public void update(float dt) {
        for (Map.Entry<Integer, VirtualMass> entry : masses.entrySet()) {
            entry.getValue().update(dt, planets);
        }
    }
}
