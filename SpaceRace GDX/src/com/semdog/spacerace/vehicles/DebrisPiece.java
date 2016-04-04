package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.effects.DustPuff;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

public class DebrisPiece extends Mass {

	private Sprite sprite;
	private float rotationalSpeed;

	private float w, h;

	private int bounces;

	public DebrisPiece(float x, float y, float dx, float dy, Planet environment, Ship ship) {
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
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}

	/***
	 * Debris pieces are to bounce twice before exploding into a puff of colored smoke.
	 * This function works out if it is strong enough to bounce and, if so, how it bounces.
	 */
	@Override
	protected void handleCollision(float speed) {
		if (bounces < 1) {
			float normal = MathUtils.atan2(y - environment.getY(), x - environment.getX());
			float velDirection = MathUtils.atan2(dy, dx);
			float diff = normal - velDirection;
			float xm = MathUtils.sin(diff);
			float ym = MathUtils.cos(diff);
			dx = dx * xm;
			dy = dy * ym;
			bounces++;
		} else {
			alive = false;
			super.handleCollision(speed);
			Universe.currentUniverse.addEffect(new DustPuff(x, y, environment.getColor()));
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

}
