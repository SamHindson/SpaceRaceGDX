package com.semdog.spacerace.universe;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.semdog.spacerace.players.Player;

public class GoalChecker {
	private HashMap<String, Boolean> booleans;
	private HashMap<String, Float> floats;
	private HashMap<String, String> strings;
	
	private int totalCriteria = 0;
	
	private boolean victory;
	
	@SuppressWarnings("unused")
	private float timeLeft;

	public GoalChecker() {
		booleans = new HashMap<>();
		floats = new HashMap<>();
		strings = new HashMap<>();
	}
	
	public void setTimeLeft(float timeLeft) {
		this.timeLeft = timeLeft;
	}

	public void addBoolean(String id, boolean value) {
		booleans.put(id, value);
		totalCriteria++;
	}

	public void addFloat(String id, float value) {
		floats.put(id, value);
		totalCriteria++;
	}

	public void addString(String id, String value) {
		strings.put(id, value);
		totalCriteria++;
	}

	public void update(Player player) {
		int correctCriteria = 0;
		
		for (Entry<String, Boolean> entry : booleans.entrySet()) {
		    String key = entry.getKey();
		    boolean value = entry.getValue();
		    
		    try {
				if(Player.class.getMethod(key).invoke(player).equals(value)) {
					correctCriteria++;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		
		for (Entry<String, Float> entry : floats.entrySet()) {
		    String key = entry.getKey();
		    float value = entry.getValue();
		    
		    try {
				if(Player.class.getMethod(key).invoke(player).equals(value)) {
					correctCriteria++;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		
		for (Entry<String, String> entry : strings.entrySet()) {
		    String key = entry.getKey();
		   	String value = entry.getValue();
		    
		    try {
				if(Player.class.getDeclaredMethod(key).invoke(player).equals(value)) {
					correctCriteria++;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(correctCriteria == totalCriteria) {
			victory = true;
		}
	}

	public boolean isVictory() {
		return victory;
	}
}