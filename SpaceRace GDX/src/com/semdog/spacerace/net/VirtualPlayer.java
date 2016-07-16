package com.semdog.spacerace.net;

public class VirtualPlayer {
	private String name;
	private int id;
	private int team;
	private float x, y, dx, dy;
	
	public VirtualPlayer() {

	}
	
	public VirtualPlayer(String name, int id, int team, float x, float y) {
		this.name = name;
		this.id = id;
		this.team = team;
		this.x = x;
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getTeam() {
		return team;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setPosition(float x2, float y2) {
		this.x = x2;
		this.y = y2;
	}
}
