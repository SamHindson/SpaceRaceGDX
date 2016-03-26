package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.weapons.Bullet;

public class SmallBombarder extends Ship {

	public SmallBombarder(float x, float y, Planet environment) {
		super(x, y, 20000, 50, environment, "runt");
		
		pCooldown = 0.2f;
		dx = 320;
	}
	
	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
	}

	@Override
	public void updateControls(float dt) {
		if(Gdx.input.isKeyPressed(Keys.A)) {
			r += dt * 30;
		}
		if(Gdx.input.isKeyPressed(Keys.D)) {
			r -= dt * 30;
		}
		
		if(Gdx.input.isKeyPressed(Keys.W) && currentFuel > 0) {
			currentFuel -= power * dt;
			dx -= 155 * dt * MathUtils.sin(r * MathUtils.degreesToRadians);
			dy += 155 * dt * MathUtils.cos(r * MathUtils.degreesToRadians);
		} else {
			
		}
		
		pRest += dt;
		sRest += dt;
		
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if(pRest > pCooldown) {
				firePrimary();
				pRest = 0;
			} 
		}
	}
	
	@Override
	protected float getImpactThreshhold() {
		return 5;
	}

	@Override
	public void firePrimary() {
		Universe.currentUniverse.addBullet(new Bullet(x, y, dx, dy, r * MathUtils.degreesToRadians + MathUtils.PI / 2.f, 1000));
	}

	@Override
	public void fireSecondary() {

	}

}
