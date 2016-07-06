package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.players.VitalSigns;
import com.semdog.spacerace.players.VitalSigns.Type;
import com.semdog.spacerace.players.Vitality;
import com.semdog.spacerace.universe.Grenade;
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
	protected Sprite sprite, silhouette;
	protected float totalFuel, currentFuel;
	protected float r, width, height;
	protected float power;
	protected float boostPower = 300;

	protected float beepTime;

	protected int pAmmo, sAmmo;
	protected boolean pAutomatic, sAutomatic;
	protected float pCooldown, sCooldown, pRest, sRest;
	protected boolean boostActive = false;
	protected float boostTime = 30, boostRemaining;
	protected VitalSigns vitalSigns;
	protected Vitality vFuel, vBoost, vHealth;
    private boolean initialized;

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
		silhouette = new Sprite(Art.get(textureName + "_sil"));
		sprite.setOriginCenter();
		width = sprite.getWidth();
		height = sprite.getHeight();

		Universe.currentUniverse.addShip(this);

		beepTime = MathUtils.random();

		this.id = id;

		vitalSigns = new VitalSigns();
		vFuel = new Vitality() {
			@Override
			public Type getValueType() {
				return Type.CONTINUOUS;
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
			public Color getColor() {
				return Colors.V_FUEL;
			}

			@Override
			public String getID() {
				return "fuel";
			}
		};

		vHealth = new Vitality() {
			@Override
			public Type getValueType() {
				return Type.CONTINUOUS;
			}

			@Override
			public float getValue() {
				return currentHealth;
			}

			@Override
			public float getMaxValue() {
				return maxHealth;
			}

			@Override
			public Color getColor() {
				return Colors.V_SHIPHEALTH;
			}

			@Override
			public String getID() {
				return "shiphealth";
			}
		};

		vBoost = new Vitality() {
			@Override
			public Type getValueType() {
				return Type.CONTINUOUS;
			}

			@Override
			public float getValue() {
				return boostRemaining;
			}

			@Override
			public float getMaxValue() {
				return boostTime;
			}

			@Override
			public Color getColor() {
				return Colors.V_BOOST;
			}

			@Override
			public String getID() {
				return "boost";
			}
		};

		vitalSigns.addItems(vHealth, vFuel);
		setBoostActive(true);
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		sprite.setRotation(r);
		sprite.setPosition(position.x - width / 2, position.y - height / 2);

		if (boostActive) {
			boostRemaining -= dt;

			if (boostRemaining <= 0) {
				setBoostActive(false);
			}
		}

		if (pilot == null) {
			beepTime += dt;

			if (beepTime > 7.7f) {
				beepTime = 0;
				Universe.currentUniverse.playSound("beep", position.x, position.y, 0.5f);
			}
		}
		
		if(ouchTime > 0) {
			silhouette.setRotation(r);
			silhouette.setPosition(position.x - width / 2, position.y - height / 2);
		}
	}

	protected float getCurrentPower() {
		return power + boostPower;
	}

	@Override
	protected void setEnvironment(Planet planet) {
		super.setEnvironment(planet);

		if (!initialized) {
			initialized = true;
			r = (-MathUtils.PI / 2 + MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX()))
					* MathUtils.radiansToDegrees;
		}

		if (pilot != null) {
			pilot.setEnvironment(planet);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		//if (boostActive)
		//	sprite.setColor(MathUtils.random());
		
		sprite.draw(batch);
		
		if(ouchTime > 0) {
			silhouette.setAlpha(MathUtils.random(0.5f, 1));
			silhouette.draw(batch);
		}

		//if (boostActive)
		//	sprite.setColor(Color.WHITE);
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

	public void die(DamageCause reason) {
		super.die(reason);
		alive = false;
		explode(reason);
	}

	public void setBoostActive(boolean boostActive) {
		this.boostActive = boostActive;

		if (boostActive) {
			vitalSigns.addItems(vBoost);
			boostRemaining = 30;
		} else {
			vitalSigns.removeItems("boost");
		}
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

        rud();
    }

    /**
     * This code is executed when the ship undergoes a RUD (Rapid Unscheduled Disassembly)
     */
    protected void rud() {
        for (int k = 0; k < 30; k++) {
            new DebrisPiece(position.x, position.y, velocity.x, velocity.y, environment, this);
        }
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

	public float getAltitude() {
		return Vector2.dst(position.x, position.y, environment.getX(), environment.getY());
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	protected void handleMassCollision(Mass mass) {
		super.handleMassCollision(mass);

		if (mass instanceof Grenade) {
			System.out.println("Collided with grenade!");
			((Grenade) mass).explode();
		}
	}

	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public boolean hasPilot() {
		return pilot != null;
	}
	
	public void orbit(float direction) {
		if(environment == null)
			findEnvironment();
		angle = MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());
		float v = (float)Math.sqrt(Universe.GRAVITY * environment.getMass() / distance(environment)) * -direction;
		velocity.x = MathUtils.sin(angle) * v;
		velocity.y = MathUtils.cos(angle) * v;
	}

    @Override
    public Color getColor() {
        return Colors.V_FUEL;
    }

    public float getFX() {
        return position.x;
    }

    public float getFY() {
        return position.y;
    }
}
