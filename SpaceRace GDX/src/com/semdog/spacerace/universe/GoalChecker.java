package com.semdog.spacerace.universe;

import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;

public class GoalChecker {
	private HashMap<String, Boolean> booleans;
	private HashMap<String, Float> floats;
	private HashMap<String, String> strings;
	
	private int totalCriteria = 0;
	
	private boolean victory;

	public GoalChecker() {
		booleans = new HashMap<>();
		floats = new HashMap<>();
		strings = new HashMap<>();
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

    public void update(Universe universe) {
        int correctCriteria = 0;
		
		for (Entry<String, Boolean> entry : booleans.entrySet()) {
		    String key = entry.getKey();
		    boolean value = entry.getValue();
		    
		    try {
                if (Universe.class.getMethod(key).invoke(universe).equals(value)) {
                    correctCriteria++;
				}
            } catch (Exception e) {
                Gdx.app.error("GoalChecker", "Something went wrong checking " + key + "!");
            }
		}
		
		for (Entry<String, Float> entry : floats.entrySet()) {
		    String key = entry.getKey();
		    float value = entry.getValue();
		    
		    try {
                if (Universe.class.getMethod(key).invoke(universe).equals(value)) {
                    correctCriteria++;
				}
            } catch (Exception e) {
                Gdx.app.error("GoalChecker", "Something went wrong checking " + key + "!");
            }
		}
		
		for (Entry<String, String> entry : strings.entrySet()) {
		    String key = entry.getKey();
		   	String value = entry.getValue();
		    
		    try {
                if (Universe.class.getDeclaredMethod(key).invoke(universe).equals(value)) {
                    correctCriteria++;
				}
            } catch (Exception e) {
                Gdx.app.error("GoalChecker", "Something went wrong checking " + key + "!");
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