package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.weapons.Bullet;

public class SmallBombarder extends Ship {
	private ParticleEffect particleEffect;

	public SmallBombarder(float x, float y, Planet environment) {
		super(x, y, 32, 32, 30000, 50, environment, "runt");

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("assets/effects/runtflame.p"), Gdx.files.internal("assets/effects"));
		particleEffect.setPosition(x, y);

		pCooldown = 0.02f;
		dx = 0;
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		particleEffect.setPosition(x, y);
		particleEffect.getEmitters().get(0).getAngle().setHigh(getAngle() * MathUtils.radiansToDegrees - 90);
		particleEffect.update(dt);
	}

	@Override
	public void updateControls(float dt) {
		if (Gdx.input.isKeyPressed(Keys.A)) {
			r += dt * 150;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			r -= dt * 150;
		}

		if (Gdx.input.isKeyPressed(Keys.W) && currentFuel > 0) {
			currentFuel -= power * dt;
			dx -= 250 * dt * MathUtils.sin(r * MathUtils.degreesToRadians);
			dy += 250 * dt * MathUtils.cos(r * MathUtils.degreesToRadians);
			particleEffect.start();
			Universe.currentUniverse.loopSound("runt", x, y, 0.1f);
		} else {
			particleEffect.allowCompletion();
			Universe.currentUniverse.stopSound("runt");
		}

		pRest += dt;
		sRest += dt;

		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (pRest > pCooldown) {
				firePrimary();
				pRest = 0;
			}
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		particleEffect.draw(batch);
		super.render(batch);
	}

	@Override
	public void debugRender(ShapeRenderer renderer) {
		super.debugRender(renderer);
	}

	@Override
	protected float getImpactThreshhold() {
		return 250;
	}

	@Override
	public void firePrimary() {
		float xo = width * MathUtils.sin(-r * MathUtils.degreesToRadians);
		float yo = width * MathUtils.cos(-r * MathUtils.degreesToRadians);
		Universe.currentUniverse.addBullet(
				new Bullet(x + xo, y + yo, dx, dy, r * MathUtils.degreesToRadians + MathUtils.PI / 2.f, 1000));
	}

	@Override
	public void fireSecondary() {

	}

}
