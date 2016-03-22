package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.universe.Planet;

public class Bullet {
	private float x, y, dx, dy, age, life = 5;
	
	@SuppressWarnings("unused")
	private int damage;

	public Bullet(float x, float y, float angle, int damage) {
		this.x = x;
		this.y = y;
		this.dx = 500 * MathUtils.cos(angle);
		this.dy = 500 * MathUtils.sin(angle);
		this.damage = damage;
	}

	public void update(float dt, Array<Planet> planets) {
		x += dx * dt;
		y += dy * dt;

		age += dt;
		
		for(Planet planet : planets) {
			if(Vector2.dst(planet.getX(), planet.getY(), x, y) < planet.getRadius()) {
				System.out.println("Bullet is dead.");
				age = life + 1;
			}
		}
	}

	public void draw(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.line(x, y, x - dx * 0.005f, y - dy * 0.005f);
	}

	public boolean alive() {
		return age < life;
	}
}
