package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.graphics.effects.DustPuff;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

class DebrisPiece extends Mass {

	private Sprite sprite;
	private float rotationalSpeed;

	private int bounces;

	DebrisPiece(float x, float y, float dx, float dy, Planet environment, Ship ship) {
		super(x, y, dx + MathUtils.random(500) - 250, dy + MathUtils.random(500) - 250, 10,
				MathUtils.random(ship.getWidth() / 2), MathUtils.random(ship.getHeight() / 2), environment, "debris");

		int w = (int) getWidth();
		int h = (int) getHeight();

		int tx = MathUtils.random((int) ship.getWidth() - 4);
		int ty = MathUtils.random((int) ship.getHeight() - 4);

		sprite = new Sprite(new TextureRegion(ship.getTexture(), tx, ty, w, h));
		sprite.setPosition(x, y);
		sprite.setSize(w, h);
		sprite.setOriginCenter();
		rotationalSpeed = MathUtils.random(0, 720);

		shouldCollide = false;
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		sprite.setPosition(position.x, position.y);
		sprite.rotate(rotationalSpeed * dt);
	}

	@Override
	protected float getImpactThreshold() {
		return 0;
	}

	@Override
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}

	@Override
	public void checkCollisions(Array<Mass> masses) {

	}

	/***
	 * Debris pieces are to bounce twice before exploding into a puff of colored
	 * smoke. This function works out if it is strong enough to bounce and, if
	 * so, how it bounces.
	 */
	@Override
	protected void handlePlanetCollision(float speed, boolean planet) {
		if (bounces < 3) {
			float surfaceAngle = MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());
			float velocityAngle = MathUtils.atan2(velocity.x, velocity.y);
			float impactAngle = (MathUtils.PI / 2.f + surfaceAngle) - velocityAngle;
			velocity.x *= MathUtils.cos(impactAngle);
			velocity.y *= MathUtils.sin(impactAngle);
			position.x += velocity.x * 0.016f;
			position.y += velocity.y * 0.016f;
			bounces++;
		} else {
			alive = false;
			super.handlePlanetCollision(speed, true);
			Universe.currentUniverse.addEffect(new DustPuff(position.x, position.y, environment.getColor()));
			Universe.currentUniverse.playSound("shrap" + Tools.decide("1", "2", "3"), position.x, position.y, 0.2f);
		}
	}

	@Override
	protected void hitPlayer(Player player) {
		Universe.currentUniverse.playerHurt(player, getVelocity().len(), DamageCause.DEBRIS);
		Universe.currentUniverse.addEffect(new DustPuff(position.x, position.y, Colors.PLANETRED));
		alive = false;
	}
	
	@Override
	public String getID() {
		return "debris";
	}

}
