package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.universe.Grenade;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.weapons.Carbine;
import com.semdog.spacerace.weapons.Weapon;

public class Player {

	private Team team = Team.PINK;

	private float health = 200;

	private Planet environment;
	private float distance, angle;

	private boolean onGround = false;
	private boolean lefting = false, righting = false;
	private boolean sprinting = false;
	private boolean alive = true;

	private boolean pilotingShip = false;
	private Ship ship;

	// private Sprite sprite;
	private Animation animation;
	private TextureRegion idleTexture;
	private float animTime = 0f;

	private float x, y, dx, dy, wx, wy;
	private float ax, ay, a;

	private Rectangle bounds;

	private Weapon weapon;
	private float da;
	private boolean boarding;

	private Ship boardingShip;

	public Player(float x, float y, Planet planet) {
		environment = planet;
		distance = Vector2.dst(x, y, planet.getX(), planet.getY());
		angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());

		this.x = x;
		this.y = y;

		TextureAtlas textureAtlas = new TextureAtlas("assets/graphics/runboy.atlas");

		animation = new Animation(1 / 30f, textureAtlas.getRegions());

		idleTexture = new TextureRegion(new Texture(Gdx.files.internal("assets/graphics/idledude.png")));

		// sprite = new Sprite(textureAtlas.findRegion("02"));
		// sprite.setSize(20, 20);

		bounds = new Rectangle(x - 10, y - 10, 20, 20);

		weapon = new Carbine();
		weapon.pickup(this);

		// textureAtlas.dispose();
	}

	public boolean isPilotingShip() {
		return pilotingShip;
	}

	public void setShip(Ship ship) {
		pilotingShip = true;
		boarding = false;
		this.ship = ship;
		ship.setPilot(this);
	}

	public Team getTeam() {
		return team;
	}

	public void update(float dt, OrthographicCamera camera) {
		if (alive) {
			if (pilotingShip) {
				ship.updateControls(dt);

				if (Gdx.input.isKeyJustPressed(Keys.E)) {
					pilotingShip = false;
					x = ship.getX();
					y = ship.getY();
					dx = ship.getDx();
					dy = ship.getDy();
					ship.setPilot(null);
					Universe.currentUniverse.playUISound("egress");
				}
			} else {
				animTime += dt;

				distance = Vector2.dst(environment.getX(), environment.getY(), x, y);
				angle = MathUtils.atan2(y - environment.getY(), x - environment.getX());

				onGround = distance < environment.getRadius() + 10;

				if (!onGround) {
					float force = (float) (Universe.GRAVITY * 100 * environment.getMass() / Math.pow(distance, 2));
					float ax = -dt * force * MathUtils.cos(angle);
					float ay = -dt * force * MathUtils.sin(angle);

					ax /= 100;
					ay /= 100;

					dx += ax * dt * 100;
					dy += ay * dt * 100;
				} else {
					dx = 0;
					dy = 0;
				}
				
				sprinting = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);

				if (Gdx.input.isKeyPressed(Keys.A)) {
					// Move anti-clockwise around planet
					float speed = sprinting ? -300 : -100;
					wx = speed * MathUtils.cos(angle - MathUtils.PI / 2.f);
					wy = speed * MathUtils.sin(angle - MathUtils.PI / 2.f);
					lefting = true;
					righting = false;
				} else if (Gdx.input.isKeyPressed(Keys.D)) {
					// Move clockwise around planet
					float speed = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? 300 : 100;
					wx = speed * MathUtils.cos(angle - MathUtils.PI / 2.f);
					wy = speed * MathUtils.sin(angle - MathUtils.PI / 2.f);
					righting = true;
					lefting = false;
				} else {
					// Bly stil

					wx = wy = 0;
					lefting = righting = false;
				}

				if (Gdx.input.isKeyPressed(Keys.SPACE) && onGround) {
					// Jump!

					// Works out which direction is up and shoots the player
					// there
					float jx = 10000 * MathUtils.cos(angle);
					float jy = 10000 * MathUtils.sin(angle);

					dx += jx * dt;
					dy += jy * dt;

					onGround = false;

					Universe.currentUniverse.playUISound("jump");
				}

				if (boarding && Gdx.input.isKeyJustPressed(Keys.E)) {
					pilotingShip = true;
					setShip(boardingShip);
					boardingShip = null;

					Universe.currentUniverse.playUISound("ingress");
				}

				x += dx * dt;
				y += dy * dt;

				x += wx * dt;
				y += wy * dt;

				bounds.setPosition(x - 10, y - 10);

				// AIMING
				ax = Gdx.input.getX();
				ay = Gdx.input.getY();

				a = -MathUtils.atan2(ay - (Gdx.graphics.getHeight() / 2), ax - (Gdx.graphics.getWidth() / 2)) + angle
						- MathUtils.PI / 2;

				if (weapon != null)
					weapon.update(dt, a);

				if (Gdx.input.isKeyJustPressed(Keys.G)) {
					float gx = x + 10 * MathUtils.cos(a);
					float gy = y + 10 * MathUtils.sin(a);

					float gdx = 375 * MathUtils.cos(a);
					float gdy = 375 * MathUtils.sin(a);

					new Grenade(gx, gy, gdx, gdy, 10, environment);
				}
			}
		}
	}

	public void draw(SpriteBatch batch) {
		if (!pilotingShip) {
			if (alive) {
				// batch.draw(animation.getKeyFrame(0, true), x - 10, y - 10,
				// 10,
				// 10, 20, 20, 1, 1, getAngle() * MathUtils.radiansToDegrees);
				float m = sprinting ? 3 : 1;
				if (righting)
					batch.draw(animation.getKeyFrame(animTime * m, true), x - 10, y - 10, 10, 10, 20, 20, 1, 1,
							getAngle() * MathUtils.radiansToDegrees);
				else if (lefting)
					batch.draw(animation.getKeyFrame(animTime * m, true), x - 10, y - 10, 10, 10, 20, 20, -1, 1,
							getAngle() * MathUtils.radiansToDegrees);
				else
					batch.draw(idleTexture, x - 10, y - 10, 10, 10, 20, 20, 1, 1,
							getAngle() * MathUtils.radiansToDegrees);
			}
		}
	}

	public void debugDraw(ShapeRenderer sr) {
		sr.setColor(Color.BLUE);
		// sr.rect(x - 10, y - 10, 20, 20);
	}

	public float getX() {
		return pilotingShip ? ship.getX() : (x + dx * Gdx.graphics.getDeltaTime());
	}

	public float getY() {
		return pilotingShip ? ship.getY() : (y + dy * Gdx.graphics.getDeltaTime());
	}

	public float getFX() {
		return pilotingShip ? ship.getX() : (x + dx * Gdx.graphics.getDeltaTime());
	}

	public float getFY() {
		return pilotingShip ? ship.getY() : (y + dy * Gdx.graphics.getDeltaTime());
	}

	public float getDa() {
		return da;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void die() {
		alive = false;
		// weapon = null;
		ship = null;
		pilotingShip = false;
	}

	public float getAngle() {
		return pilotingShip ? ship.getAngle() : (angle - MathUtils.PI / 2) % MathUtils.PI2;
	}

	public float getDX() {
		return dx;
	}

	public float getDY() {
		return dy;
	}

	public void spawn(float x, float y) {
		this.x = x;
		this.y = y;
		alive = true;
	}

	public void setBoarding(boolean boarding, Ship boardingShip) {
		this.boarding = boarding;
		this.boardingShip = boardingShip;
	}

	public boolean isBoarding() {
		return boarding;
	}

	public void doDamage(float amount, DeathCause cause) {
		if (alive) {
			health -= amount;

			Universe.currentUniverse.playUISound("playerhit" + Tools.decide(1, 2, 3, 4, 5));

			if (health < 0) {
				health = 0;
				Universe.currentUniverse.playerKilled(this, cause);
			}
		}
	}

	public void addSpeed(float dx2, float dy2) {
		dx += dx2;
		dy += dy2;
	}

	public float getVelocity() {
		return Vector2.dst(0, 0, dx, dy);
	}

	public void setEnvironment(Planet planet) {
		environment = planet;
	}
}
