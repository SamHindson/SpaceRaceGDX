package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.universe.Planet;

public class CrapLander extends Ship {

	private ParticleEffect particleEffect;

	public CrapLander(float x, float y, Planet environment) {
		super(x, y, 32, 32, 10000, 1000, environment, "tinyship");

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("assets/effects/landerflame.p"), Gdx.files.internal("assets/effects"));
		particleEffect.setPosition(x, y);
		particleEffect.start();
		
		dy = -350;
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		particleEffect.setPosition(x, y);
		particleEffect.getEmitters().get(0).getAngle().setHigh(getAngle() * MathUtils.radiansToDegrees - 90);
		particleEffect.update(dt);
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
	public void updateControls(float dt) {
		if (Gdx.input.isKeyPressed(Keys.A)) {
			r += dt * 30;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			r -= dt * 30;
		}

		if (Gdx.input.isKeyPressed(Keys.W) && currentFuel > 0) {
			currentFuel -= power * dt;
			dx -= 155 * dt * MathUtils.sin(r * MathUtils.degreesToRadians);
			dy += 155 * dt * MathUtils.cos(r * MathUtils.degreesToRadians);
			particleEffect.start();
		} else {
			particleEffect.allowCompletion();
		}
	}
	
	@Override
	protected float getImpactThreshhold() {
		return 1000;
	}

	@Override
	public void firePrimary() {
		System.out.println("No weapons mate");
	}

	@Override
	public void fireSecondary() {
		System.out.println("Werent you listening");
	}

}
