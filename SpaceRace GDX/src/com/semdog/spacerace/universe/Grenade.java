package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DeathCause;
import com.semdog.spacerace.players.Player;

/***
 * Kabuum
 */

public class Grenade extends Mass {

	private boolean exploded = false;

	private Sprite sprite;

	public Grenade(float x, float y, float dx, float dy, float mass, Planet environment) {
		super(x, y, dx, dy, 2, 2, mass, environment);
		sprite = new Sprite(Art.get("grenade"));
		sprite.setSize(5, 5);
		sprite.setOriginCenter();

		bounds = new Rectangle(x, y, 1, 1);
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		sprite.rotate(dt * 2000);
		sprite.setPosition(x, y);
		bounds.set(x, y, 1, 1);
	}

	@Override
	protected float getImpactThreshhold() {
		return 0;
	}

	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}

	@Override
	protected void handlePlanetCollision(float speed, boolean x) {
		if (!exploded) {
			explode();
		}
	}

	@Override
	protected void hitPlayer(Player player) {
		Universe.currentUniverse.playerKilled(player, DeathCause.EXPLOSION);
		explode();
	}

	private void explode() {
		exploded = true;
		Universe.currentUniverse.addEffect(new Explosion(x, y));
	}
	
	@Override
	public boolean isAlive() {
		return !exploded;
	}

	@Override
	protected float getWidth() {
		return 1;
	}

	@Override
	protected float getHeight() {
		return 1;
	}

}
