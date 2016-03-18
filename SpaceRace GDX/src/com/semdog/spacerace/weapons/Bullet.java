package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Bullet {
	private float x, y, dx, dy, age, life = 5;
	private int damage;

	public Bullet(float x, float y, float angle, int damage) {
		this.x = x;
		this.y = y;
		this.dx = 10000 * MathUtils.cos(angle);
		this.dy = 10000 * MathUtils.sin(angle);
		this.damage = damage;
	}

	public void update(float dt) {
		x += dx * dt;
		y += dy * dt;

		age += dt;
	}

	public void draw(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.line(x, y, x + dx * 0.005f, y + dy * 0.005f);
	}

	public boolean alive() {
		return age < life;
	}
}
