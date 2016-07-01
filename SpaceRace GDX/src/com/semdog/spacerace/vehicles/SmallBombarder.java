package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.VitalSigns.Type;
import com.semdog.spacerace.players.Vitality;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.weapons.Bullet;

public class SmallBombarder extends Ship {
	private ParticleEffect particleEffect;
	
	private int maxAmmo = 128, currentAmmo;

	public SmallBombarder(float x, float y, String id) {
        super(x, y, 32, 32, 10000, 75, "runt", id);

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("assets/effects/runtflame.p"), Gdx.files.internal("assets/effects"));
		particleEffect.setPosition(x, y);
		particleEffect.allowCompletion();

		pCooldown = 0.025f;
		
		currentAmmo = maxAmmo;

		setMaxHealth(500);
		
		vitalSigns.addItems(new Vitality() {
			
			@Override
			public Type getValueType() {
				return Type.CONTINUOUS;
			}
			
			@Override
			public float getValue() {
				return currentAmmo;
			}
			
			@Override
			public float getMaxValue() {
				return maxAmmo;
			}
			
			@Override
			public String getID() {
				return "bombarderammo";
			}
			
			@Override
			public Color getColor() {
				return Colors.V_SHIPAMMO;
			}
		});
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
			if (pRest > pCooldown && currentAmmo > 0) {
				currentAmmo--;
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
		float i = MathUtils.random(-2f, 2f);
		Universe.currentUniverse
				.addBullet(new Bullet(position.x + width * MathUtils.sin(-r * MathUtils.degreesToRadians + i / 5.f),
						position.y + width * MathUtils.cos(-r * MathUtils.degreesToRadians + i / 5.f), velocity.x,
						velocity.y, r * MathUtils.degreesToRadians + MathUtils.PI / 2.f, 5, 0));
		Universe.currentUniverse.playSound("runtgun", position.x, position.y, 0.5f);
	}

	@Override
	public void fireSecondary() {

	}

	@Override
	public void setDy(float dy) {
		super.setDy(dy);
	}

	@Override
	public void setDx(float dx) {
		super.setDx(dx);
	}
	
	@Override
	public void orbit(float direction) {
		super.orbit(direction);
	}
}
