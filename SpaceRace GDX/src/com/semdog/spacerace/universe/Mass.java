package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mass {
	protected float x, y, dx, dy, mass, angle;
	protected boolean onSurface;
	protected Planet environment;

	protected static Texture texture;

	static {
		texture = new Texture(Gdx.files.internal("assets/test.png"));
	}

	public Mass(float x, float y, float mass, Planet environment) {
		this.x = x;
		this.y = y;
		this.mass = mass;
		
		this.environment = environment;
		
		dx = 0;
	}

	public void update(float dt, Array<Planet> gravitySources) {
		angle = MathUtils.atan2(y - environment.getY(), x - environment.getX());

		for (Planet planet : gravitySources) {
			if (inRange(planet) && !onSurface(planet)) {
				
				if(!environment.equals(planet)) {
					environment = planet;
				}
				
				float force = (float) (Universe.GRAVITY * mass * planet.getMass() / Math.pow(distance(planet), 2));
				float ax = -dt * force * MathUtils.cos(angle);
				float ay = -dt * force * MathUtils.sin(angle);
				
				ax /= mass;
				ay /= mass;
				
				dx += ax * dt;
				dy += ay * dt;
			}
		}

		x += dx * dt;
		y += dy * dt;
	}

	protected boolean onSurface(Planet planet) {
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

	protected boolean inRange(Planet planet) {
		return distance(planet) < planet.getSOI();
	}

	protected float distance(Planet planet) {
		return Vector2.dst(x, y, planet.getX(), planet.getY());
	}

	public void debugRender(SpriteBatch batch) {
		batch.draw(texture, x, y, 5, 5);
	}
}
