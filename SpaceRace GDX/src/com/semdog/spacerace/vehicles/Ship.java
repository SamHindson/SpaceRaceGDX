package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DeathCause;
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

	protected Player pilot;
	protected Sprite sprite;
	protected float totalFuel, currentFuel;
	protected float r, width, height;
	protected float power;

	protected float beepTime;

	protected int pAmmo, sAmmo;
	protected boolean pAutomatic, sAutomatic;
	protected float pCooldown, sCooldown, pRest, sRest;

	protected Ship(float x, float y, float w, float h, float fuel, float power, Planet environment,
			String textureName) {
		this(x, y, w, h, fuel, power, 0, 0, environment, textureName);
	}

	protected Ship(float x, float y, float w, float h, float fuel, float power, int primaryAmmo, int secondaryAmmo,
			Planet environment, String textureName) {
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

		Universe.currentUniverse.addShip(this);

		beepTime = MathUtils.random();
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		// updateControls(dt);
		super.update(dt, gravitySources);
		sprite.setRotation(r);
		sprite.setPosition(x - width / 2, y - height / 2);

		if (pilot == null) {
			beepTime += dt;

			if (beepTime > 0.5f) {
				beepTime = 0;
				Universe.currentUniverse.playSound("beep", x, y, 0.5f);
			}
		}
	}
	
	@Override
	protected void setEnvironment(Planet planet) {
		super.setEnvironment(planet);
		
		if(pilot != null) {
			pilot.setEnvironment(planet);
		}
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

	protected void die(DeathCause reason) {
		super.die(reason);
		explode(reason);
	}

	@Override
	protected void handlePlanetCollision(float speed, boolean v) {
		super.handlePlanetCollision(speed, v);
		if (speed > getImpactThreshhold()) {
			explode(DeathCause.PLANET);
		}
	}

	protected void explode(DeathCause cause) {
		Gdx.app.log("Ship", "BOOOOM!");
		Universe.currentUniverse.addEffect(new Explosion(x, y));

		if (pilot != null) {
			Universe.currentUniverse.playerKilled(pilot, cause);
		}

		for (int k = 0; k < 15; k++) {
			new DebrisPiece(x, y, dx, dy, environment, this);
		}

		alive = false;
	}

	@Override
	protected void hitPlayer(Player player) {
		player.addSpeed(dx, dy);
		player.doDamage(getVelocity(), DeathCause.SHIP);

	}

	public abstract void firePrimary();

	public abstract void fireSecondary();

	public void setPilot(Player pilot) {
		this.pilot = pilot;
	}

	public Texture getTexture() {
		return sprite.getTexture();
	}
}
