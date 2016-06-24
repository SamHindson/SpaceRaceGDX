package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.weapons.Bullet;

public class SmallBombarder extends Ship {
	private ParticleEffect particleEffect;

	public SmallBombarder(float x, float y, String id) {
		super(x, y, 32, 32, 10000, 400, "runt", id);

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("assets/effects/runtflame.p"), Gdx.files.internal("assets/effects"));
		particleEffect.setPosition(x, y);
		particleEffect.allowCompletion();

		pCooldown = 0.02f;

		setMaxHealth(100);
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		particleEffect.setPosition(position.x, position.y);
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

		if (Gdx.input.isKeyPressed(Keys.W) && currentFuel > 1) {
			currentFuel -= power * dt;
			velocity.x -= getCurrentPower() * dt * MathUtils.sin(r * MathUtils.degreesToRadians);
			velocity.y += getCurrentPower() * dt * MathUtils.cos(r * MathUtils.degreesToRadians);
			particleEffect.start();
			Universe.currentUniverse.loopSound("runt", position.x, position.y, -1f);
			Universe.currentUniverse.setCameraShake(2);
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
	protected void explode(DamageCause cause) {
		super.explode(cause);
		Universe.currentUniverse.stopSound("runt");
	}

	@Override
	public void debugRender(ShapeRenderer renderer) {
		super.debugRender(renderer);
	}

	@Override
	protected float getImpactThreshold() {
		return 250;
	}

	@Override
	public void firePrimary() {
		for (float i = -2; i <= 2; i++) {
			Universe.currentUniverse
					.addBullet(new Bullet(position.x + width * MathUtils.sin(-r * MathUtils.degreesToRadians + i / 5.f),
							position.y + width * MathUtils.cos(-r * MathUtils.degreesToRadians + i / 5.f), velocity.x, velocity.y,
							r * MathUtils.degreesToRadians + MathUtils.PI / 2.f, 1));
		}
	}

	@Override
	public void fireSecondary() {

	}

	@Override
	public int getColor() {
		return 0x3FDD4DFF;
	} 
	
	@Override
	public void setDy(float dy) {
		super.setDy(dy);
	}
	
	@Override
	public void setDx(float dx) {
		super.setDx(dx);
	}
}
