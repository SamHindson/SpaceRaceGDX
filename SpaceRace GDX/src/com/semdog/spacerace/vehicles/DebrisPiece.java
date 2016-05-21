package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.graphics.effects.DustPuff;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.players.DeathCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

class DebrisPiece extends Mass {

	private Sprite sprite;
	private float rotationalSpeed;

	private float w, h;

	private int bounces;

	DebrisPiece(float x, float y, float dx, float dy, Planet environment, Ship ship) {
		super(x, y, dx + MathUtils.random(500) - 250, dy + MathUtils.random(500) - 250, 10, 10, 50, environment);

		int w = (int) MathUtils.random(ship.getWidth() / 2);
		int h = (int) MathUtils.random(ship.getHeight() / 2);

		int tx = MathUtils.random((int) ship.getWidth() - 4);
		int ty = MathUtils.random((int) ship.getHeight() - 4);

		sprite = new Sprite(new TextureRegion(ship.getTexture(), tx, ty, w, h));
		sprite.setPosition(x, y);
		sprite.setSize(w, h);
		sprite.setOriginCenter();
		rotationalSpeed = MathUtils.random(0, 720);

		shouldCollide = false;

		this.w = w;
		this.h = h;
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		sprite.setPosition(x, y);
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
	 * Debris pieces are to bounce twice before exploding into a puff of colored smoke.
	 * This function works out if it is strong enough to bounce and, if so, how it bounces.
	 */
	@Override
	protected void handlePlanetCollision(float speed, boolean planet) {
        if (bounces < 3) {
            float surfaceAngle = MathUtils.atan2(y - environment.getY(), x - environment.getX());
            float velocityAngle = MathUtils.atan2(dy, dx);
            float impactAngle = (MathUtils.PI / 2.f + surfaceAngle) - velocityAngle;
            dx *= MathUtils.cos(impactAngle);
            dy *= MathUtils.sin(impactAngle);
            x += dx * 0.016f;
            y += dy * 0.016f;
            bounces++;
        } else {
			alive = false;
			super.handlePlanetCollision(speed, true);
			Universe.currentUniverse.addEffect(new DustPuff(x, y, environment.getColor()));
			Universe.currentUniverse.playSound("shrap" + Tools.decide("1", "2", "3"), x, y, 0.2f);
		}
	}

	@Override
	protected float getWidth() {
		return w;
	}

	@Override
	protected float getHeight() {
		return h;
	}

	@Override
	protected void hitPlayer(Player player) {
		Universe.currentUniverse.playerHurt(player, getVelocity(), DeathCause.DEBRIS);
		Universe.currentUniverse.addEffect(new DustPuff(x, y, Colors.PLANETRED));
		alive = false;
	}

}
