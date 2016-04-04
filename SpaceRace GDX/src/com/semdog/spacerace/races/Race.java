package com.semdog.spacerace.races;

import java.io.Serializable;
import java.util.HashMap;

public class Race implements Serializable {

	private String name, description;
	private HashMap<String, String> parameters;
	private float bestTime;
	
	public Race(String name) {
		this.name = name;
		
		parameters = new HashMap<>();
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public float getBestTime() {
		return bestTime;
	}
	
	public void setBestTime(float bestTime) {
		this.bestTime = bestTime;
	}
}
