package com.semdog.spacerace.net;

import com.badlogic.gdx.math.Vector2;

public class PlayerPosition {
	private int id;
	private Vector2 position;
	
	public PlayerPosition() {

	}
	
	public PlayerPosition(int id, Vector2 position) {
		this.id = id;
		this.position = position;
	}

	public Vector2 getPosition() {
		return position;
	}
	
	public int getID() {
		return id;
	}
}
