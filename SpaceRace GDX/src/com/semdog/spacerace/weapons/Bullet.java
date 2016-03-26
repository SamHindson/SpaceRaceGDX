package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.effects.DustPuff;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

public class Bullet {
	private float x, y, dx, dy, age, life = 5;
	
	@SuppressWarnings("unused")
	private int damage;

	public Bullet(float x, float y, float dx, float dy, float angle, int damage) {
		this.x = x;
		this.y = y;
		this.dx = 500 * MathUtils.cos(angle) + dx;
		this.dy = 500 * MathUtils.sin(angle) + dy;
		this.damage = damage;
	}

	public void update(float dt, Array<Planet> planets) {
		for(Planet planet : planets) {
			float d = Vector2.dst(planet.getX(), planet.getY(), x, y);
			float a = MathUtils.atan2(y - planet.getY(), x - planet.getX());
			
			dx += -planet.getGravity(d) * MathUtils.cos(a) * dt * 0.2f;
			dy += -planet.getGravity(d) * MathUtils.sin(a) * dt * 0.2f;
			
			if(d < planet.getRadius()) {
				float px = planet.getX() + MathUtils.cos(a) * planet.getRadius();
				float py = planet.getY() + MathUtils.sin(a) * planet.getRadius();
				Universe.currentUniverse.addEffect(new DustPuff(px, py, planet.getColor()));
				age = life + 1;
			}
		}
		
		x += dx * dt;
		y += dy * dt;
	}

	public void draw(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.line(x, y, x - dx * 0.005f, y - dy * 0.005f);
	}

	public boolean alive() {
		return age < life;
	}
}
