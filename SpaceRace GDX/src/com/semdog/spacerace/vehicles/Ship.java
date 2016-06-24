package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.players.VitalSigns.Type;
import com.semdog.spacerace.players.Vitality;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

/***
 * The ship class, which all of the in-game vehicles derive from.
 * 
 * @author Sam
 */

public abstract class Ship extends Mass implements Vitality {

	protected Player pilot;
	protected Sprite sprite;
	protected float totalFuel, currentFuel;
	protected float r, width, height;
	protected float power;
	protected float boostPower;

	protected float beepTime;

	protected int pAmmo, sAmmo;
	protected boolean pAutomatic, sAutomatic;
	protected float pCooldown, sCooldown, pRest, sRest;
	
	private boolean initialized;
	
	protected Boost boost;
	protected boolean boostActive = false;
	protected float boostTime = 0;

	protected Ship(float x, float y, float w, float h, float fuel, float power, String textureName, String id) {
		this(x, y, w, h, fuel, power, 0, 0, textureName, id);
	}

	protected Ship(float x, float y, float w, float h, float fuel, float power, int primaryAmmo, int secondaryAmmo,
			String textureName, String id) {
		super(x, y, 0, 0, 5000, w, h, null, id);

		this.pAmmo = primaryAmmo;
		this.sAmmo = secondaryAmmo;

		totalFuel = currentFuel = fuel;

		this.power = power;

		sprite = new Sprite(Art.get(textureName));
		sprite.setOriginCenter();
		width = sprite.getWidth();
		height = sprite.getHeight();

		Universe.currentUniverse.addShip(this);

		beepTime = MathUtils.random();
		
		this.id = id;
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		sprite.setRotation(r);
		sprite.setPosition(position.x - width / 2, position.y - height / 2);
		
		if (pilot == null) {
			beepTime += dt;

			if (beepTime > 0.5f) {
				beepTime = 0;
				Universe.currentUniverse.playSound("beep", position.x, position.y, 0.5f);
			}
		}
	}
	
	protected float getCurrentPower() {
		return power + boostPower;
	}

	@Override
	protected void setEnvironment(Planet planet) {
		super.setEnvironment(planet);

		if(!initialized) {
			initialized = true;
			r = (-MathUtils.PI / 2 + MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX())) * MathUtils.radiansToDegrees;
		}
		
		if (pilot != null) {
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
	
	public float getAngleAroundEnvironment() {
		return MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	protected void die(DamageCause reason) {
		super.die(reason);
		explode(reason);
	}

	@Override
	protected void handlePlanetCollision(float speed, boolean v) {
		super.handlePlanetCollision(speed, v);
	}

	protected void explode(DamageCause cause) {
        Universe.currentUniverse.addEffect(new Explosion(position.x, position.y));

		if (pilot != null) {
			Universe.currentUniverse.playerKilled(pilot, cause);
		}

		for (int k = 0; k < 15; k++) {
			new DebrisPiece(position.x, position.y, velocity.x, velocity.y, environment, this);
		}

		alive = false;
	}

	@Override
	protected void hitPlayer(Player player) {
		player.addSpeed(velocity);
		player.doDamage(getVelocity().len(), DamageCause.SHIP);
	}

	public abstract void firePrimary();

	public abstract void fireSecondary();

	public void setPilot(Player pilot) {
		this.pilot = pilot;
	}

	public Texture getTexture() {
		return sprite.getTexture();
	}
	
	@Override
	public float getValue() {
		return currentFuel;
	}

	@Override
	public float getMaxValue() {
		return totalFuel;
	}

	@Override
	public Type getValueType() {
		return Type.CONTINUOUS;
	}

	public float getAltitude() {
		return Vector2.dst(position.x, position.y, environment.getX(), environment.getY());
	}
	
	@Override
	public String getID() {
		return id;
	}
}

class Boost implements Vitality {

	@Override
	public float getValue() {
		return 0;
	}

	@Override
	public float getMaxValue() {
		return 10;
	}

	@Override
	public int getColor() {
		return 0x7CAFC4FF;
	}

	@Override
	public Type getValueType() {
		return Type.CONTINUOUS;
	}
	
}
