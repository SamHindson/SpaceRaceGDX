package com.semdog.spacerace.net;

public class VirtualPlanet {
	private float x, y, radius;
	private String id;
	
	public VirtualPlanet() {

	}

	public VirtualPlanet(String id, float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.id = id;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getRadius() {
		return radius;
	}
	
	public String getID() {
		return id;
	}
}
