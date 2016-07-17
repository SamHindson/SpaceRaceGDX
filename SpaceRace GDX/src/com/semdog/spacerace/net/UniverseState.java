package com.semdog.spacerace.net;

import com.semdog.spacerace.net.entities.VirtualPlanet;
import com.semdog.spacerace.net.entities.VirtualPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UniverseState {
    private VirtualPlanet[] planets;
    private VirtualPlayer[] players;
    private VirtualPlayer[] pinks, blues;
    private int[] playerIDs;

    public VirtualPlanet[] getPlanets() {
        return planets;
    }

    public void setPlanets(ArrayList<VirtualPlanet> planets) {
        this.planets = new VirtualPlanet[planets.size()];
        for (int u = 0; u < planets.size(); u++) {
            this.planets[u] = planets.get(u);
        }
    }

    public int[] getPlayerIDs() {
        return playerIDs;
    }

    public VirtualPlayer[] getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<Integer, VirtualPlayer> players) {
        int a = 0;
        this.players = new VirtualPlayer[players.size()];
        this.playerIDs = new int[players.size()];

        for (Map.Entry<Integer, VirtualPlayer> entry : players.entrySet()) {
            this.playerIDs[a] = entry.getKey();
            this.players[a] = entry.getValue();
            a++;
        }
    }
}
