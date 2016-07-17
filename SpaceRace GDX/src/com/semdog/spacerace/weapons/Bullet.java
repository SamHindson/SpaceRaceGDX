package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.effects.DustPuff;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

/**
 * The bullet is an object which zooms through the depths of space, looking for
 * someone whose day it can ruin.
 * They are more often than not fired from a gun of sorts.
 *  
 * @author Sam
 */

public class Bullet {
	private float x, y, dx, dy, age, life = MathUtils.random(2f, 5f);
	private int damage;
	private int ownerID;

	public Bullet(int ownerID, float x, float y, float dx, float dy, int damage) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.damage = damage;
		this.ownerID = ownerID;
	}

	public Bullet(float x, float y, float dx, float dy, float angle, int damage, float inaccuracy) {
		this(0, x, y, 3500 * MathUtils.cos(angle + inaccuracy) + dx, 3500 * MathUtils.sin(angle + inaccuracy) + dy, damage);
	}

	public void updatePhysics(float dt) {
		x += dx * dt;
		y += dy * dt;
		age += dt;
	}

	public void checkCollisions(Array<Planet> planets) {
		for (Planet planet : planets) {
			float d = Vector2.dst(planet.getX(), planet.getY(), x, y);
			float a = MathUtils.atan2(y - planet.getY(), x - planet.getX());

			if (d < planet.getRadius()) {
				float px = planet.getX() + MathUtils.cos(a) * planet.getRadius();
				float py = planet.getY() + MathUtils.sin(a) * planet.getRadius();
				if (MathUtils.randomBoolean(0.5f))
					Universe.currentUniverse.addEffect(new DustPuff(px, py, planet.getColor()));
				age = life + 1;
			}
		}

	}

	public void draw(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.line(x, y, x + dx * 0.005f, y + dy * 0.005f);
	}

	public boolean alive() {
		return age < life;
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

	public void die() {
		age = life * 5;
	}

	public Vector2 getVelocity() {
		return new Vector2(dx, dy);
	}

	public int getOwnerID() {
		return ownerID;
	}
}
