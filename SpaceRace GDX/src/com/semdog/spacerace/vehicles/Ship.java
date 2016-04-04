package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

/***
 * The ship class, which all of the in-game vehicles derive from.
 * 
 * @author Sam
 */

public abstract class Ship extends Mass {

	protected Player driver;
	protected Sprite sprite;
	protected float totalFuel, currentFuel;
	protected float r, width, height;
	protected float power;

	protected int pAmmo, sAmmo;
	protected boolean pAutomatic, sAutomatic;
	protected float pCooldown, sCooldown, pRest, sRest;

	protected Ship(float x, float y, float w, float h, float fuel, float power, Planet environment, String textureName) {
		this(x, y, w, h, fuel, power, 0, 0, environment, textureName);
	}

	protected Ship(float x, float y, float w, float h, float fuel, float power, int primaryAmmo, int secondaryAmmo, Planet environment,
			String textureName) {
		super(x, y, 0, 0, 5000, w, h, environment);
		this.x = x;
		this.y = y;

		this.pAmmo = primaryAmmo;
		this.sAmmo = secondaryAmmo;

		totalFuel = currentFuel = fuel;

		this.power = power;
		this.environment = environment;

		sprite = new Sprite(Art.get(textureName));
		sprite.setOriginCenter();
		width = sprite.getWidth();
		height = sprite.getHeight();
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		//updateControls(dt);
		super.update(dt, gravitySources);
		sprite.setRotation(r);
		sprite.setPosition(x - width / 2, y - height / 2);
	}

	@Override
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}

	public abstract void updateControls(float dt);

	public float getAngle() {
		return r * MathUtils.degreesToRadians;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
	
	@Override
	protected void die() {
		explode();
	}

	@Override
	protected void handleCollision(float speed) {
		super.handleCollision(speed);
		System.out.println(speed);
		if (speed > getImpactThreshhold()) {
			explode();
		}
	}

	protected void explode() {
		Universe.currentUniverse.addEffect(new Explosion(x, y, 1000));
		
		for(int k = 0; k < 15; k++) {
			new DebrisPiece(x, y, dx, dy, environment, this);
		}
		
		alive = false;
	}

	protected abstract float getImpactThreshhold();
	
	public abstract void firePrimary();
	public abstract void fireSecondary();

	public Texture getTexture() {
		return sprite.getTexture();
	}
}
