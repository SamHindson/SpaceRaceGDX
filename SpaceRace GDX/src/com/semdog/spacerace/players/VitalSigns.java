package com.semdog.spacerace.players;

import java.util.HashMap;

/**
 * Created by Sam on 07-May-16.
 * <p>
 * A little class to hold all of the player's necessary HUD information
 */
public class VitalSigns {
	private HashMap<String, Vitality> signs;

	public VitalSigns() {
		signs = new HashMap<>();
	}

	public void addItems(Vitality... vitalities) {
		for (Vitality vitality : vitalities) {
			signs.put(vitality.getID(), vitality);
		}
	}

	public void removeItems(String... ids) {
		for (String id : ids) {
			signs.remove(id);
		}
	}

	public Vitality get(String id) {
		return signs.get(id);
	}

	public HashMap<String, Vitality> getSigns() {
		return signs;
	}

	public enum Type {
		DISCRETE, CONTINUOUS
	}
}
