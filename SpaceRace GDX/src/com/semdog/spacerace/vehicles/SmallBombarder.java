package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.weapons.Bullet;

public class SmallBombarder extends Ship {

	public SmallBombarder(float x, float y, Planet environment) {
		super(x, y, 32, 32, 20000, 50, environment, "runt");

		pCooldown = 0.02f;
		dx = 350;
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
	}

	@Override
	public void updateControls(float dt) {
		if (Gdx.input.isKeyPressed(Keys.A)) {
			r += dt * 300;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			r -= dt * 300;
		}

		if (Gdx.input.isKeyPressed(Keys.W) && currentFuel > 0) {
			currentFuel -= power * dt;
			dx -= 250 * dt * MathUtils.sin(r * MathUtils.degreesToRadians);
			dy += 250 * dt * MathUtils.cos(r * MathUtils.degreesToRadians);
		} else {

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
