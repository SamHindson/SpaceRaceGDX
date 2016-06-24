package com.semdog.spacerace.races;

import java.io.Serializable;

public class Race implements Serializable {

	private static final long serialVersionUID = -6470817139582270410L;
	
	private String name, description;
	
	@SuppressWarnings("unused")
	private float yourTime, bestTime;
	
	public Race(String name) {
		this.name = name;
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
