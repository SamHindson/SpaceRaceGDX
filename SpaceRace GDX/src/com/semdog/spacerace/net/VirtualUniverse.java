package com.semdog.spacerace.net;

import java.util.ArrayList;
import java.util.HashMap;

public class VirtualUniverse {
	private HashMap<Integer, VirtualPlayer> players;
	private ArrayList<VirtualPlanet> planets;
	
	public VirtualUniverse() {
		players = new HashMap<>();
		planets = new ArrayList<>();
	}

	public void addPlanet(String id, float x, float y, float radius) {
		planets.add(new VirtualPlanet(id, x, y, radius));
	}
	
	public void addPlayer(int id, VirtualPlayer player) {
		System.out.println("Wew, new player");
		players.put(id, player);
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
}
