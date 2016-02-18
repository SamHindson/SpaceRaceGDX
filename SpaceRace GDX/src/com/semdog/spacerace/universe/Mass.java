package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mass {
	private float x, y, dx, dy, mass;
	private boolean onSurface;

	private static Texture texture;

	static {
		texture = new Texture(Gdx.files.internal("assets/test.png"));
	}

	public Mass(float x, float y, float mass) {
		this.x = x;
		this.y = y;
		this.mass = mass;
		
		dx = 50;
	}

	public void update(float dt, Array<Planet> gravitySources) {
		for (Planet planet : gravitySources) {
			if (inRange(planet) && !onSurface(planet)) {
				float force = (float) (Universe.GRAVITY * mass * planet.getMass() / Math.pow(distance(planet), 2));
				float angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());
				dx += -dt * force * MathUtils.cos(angle);
				dy += -dt * force * MathUtils.sin(angle);
			}
		}

		x += dx * dt;
		y += dy * dt;
	}

	private boolean onSurface(Planet planet) {
		if (distance(planet) <= planet.getRadius()) {
			onSurface = true;
			dx = dy = 0;
		} else {
			onSurface = false;
		}
		return onSurface;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	private boolean inRange(Planet planet) {
		return distance(planet) < planet.getSOI();
	}

	private float distance(Planet planet) {
		return Vector2.dst(x, y, planet.getX(), planet.getY());
	}

	public void debugRender(SpriteBatch batch) {
		batch.draw(texture, x, y, 5, 5);
	}
}
