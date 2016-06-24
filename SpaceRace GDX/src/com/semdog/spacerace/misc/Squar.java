package com.semdog.spacerace.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;

/**
 * Created by sam on 2016/05/01.
 *
 * A test mass to work on collision physics and all that jazz
 */
public class Squar extends Mass {

	float age;

	public Squar(float x, float y, float dx, float dy, float mass, Planet environment) {
		super(x, y, dx, dy, mass, 100, 100, environment, "Herobrine");
	}

	@Override
	protected float getImpactThreshold() {
		return 0;
	}

	@Override
	public void debugRender(ShapeRenderer renderer) {
		renderer.setColor(new Color(hashCode()));
		renderer.rect(position.x - getWidth() / 2, position.y - getHeight() / 2, getWidth(), getHeight());
	}

	@Override
	protected float getWidth() {
		return 20;
	}

	@Override
	protected void die(DamageCause reason) {
		age = 0;
	}

	@Override
	protected void handlePlanetCollision(float speed, boolean withPlanet) {
		die(DamageCause.EXPLOSION);
	}

	@Override
	protected float getHeight() {
		return 20;
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		age += dt;

		if (age > 10) {
			die(DamageCause.EXPLOSION);
			age = 0;
		}
	}

	@Override
	protected void hitPlayer(Player player) {

	}

	@Override
	public String getID() {
		return "squazzy";
	}
}
