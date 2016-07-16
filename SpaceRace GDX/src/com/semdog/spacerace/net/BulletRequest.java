package com.semdog.spacerace.net;

public class BulletRequest {
	private float x, y, dx, dy;
	private int damage;

	public BulletRequest() {
	}

	public BulletRequest(float x, float y, float dx, float dy, int damage) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.damage = damage;
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

	public int getDamage() {
		return damage;
	}
	
	
}
