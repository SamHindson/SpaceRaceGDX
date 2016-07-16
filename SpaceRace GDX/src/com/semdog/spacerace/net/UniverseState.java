package com.semdog.spacerace.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UniverseState {
	private VirtualPlanet[] planets;
	private VirtualPlayer[] players;
	private int[] playerIDs;
	
	public void setPlanets(ArrayList<VirtualPlanet> planets) {
		this.planets = new VirtualPlanet[planets.size()];
		for(int u = 0; u < planets.size(); u++) {
			this.planets[u] = planets.get(u);
		}
	}
	
	public void setPlayers(HashMap<Integer, VirtualPlayer> players) {
		System.out.println("Setting players...");
		
		int a = 0;
		this.players = new VirtualPlayer[players.size()];
		this.playerIDs = new int[players.size()];
		
		for(Map.Entry<Integer, VirtualPlayer> entry : players.entrySet()) {
			this.playerIDs[a] = entry.getKey();
			this.players[a] = entry.getValue();
			System.out.println("Plauers asses to the MAPOES");
			a++;
		}
	}
	
	public VirtualPlanet[] getPlanets() {
		return planets;
	}
	
	public int[] getPlayerIDs() {
		return playerIDs;
	}
	
	public VirtualPlayer[] getPlayers() {
		return players;
	}
}
