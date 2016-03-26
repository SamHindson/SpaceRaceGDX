package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.universe.Grenade;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.weapons.Carbine;
import com.semdog.spacerace.weapons.Weapon;

public class Player {

	private Planet environment;
	private float distance, angle;

	private boolean onGround = false;

	private boolean controllingShip = false;
	private Ship ship;

	private Sprite sprite;

	private float x, y, dx, dy, wx, wy;
	private float ax, ay, a;

	private Rectangle bounds;

	private Weapon weapon;
	private float da;

	public Player(float x, float y, Planet planet) {
		environment = planet;
		distance = Vector2.dst(x, y, planet.getX(), planet.getY());
		angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());

		this.x = x;
		this.y = y;

		sprite = new Sprite(new Texture(Gdx.files.internal("assets/dude.png")));
		sprite.setSize(20, 20);

		bounds = new Rectangle(x - 10, y - 10, 20, 20);

		weapon = new Carbine();
		weapon.pickup(this);
	}

	public void setShip(Ship ship) {
		controllingShip = true;
		this.ship = ship;
	}

	public void update(float dt, OrthographicCamera camera) {
		if (controllingShip) {
			ship.updateControls(dt);
		} else {
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

			if (Gdx.input.isKeyPressed(Keys.A)) {
				// Move anti-clockwise around planet
				wx = -100 * MathUtils.cos(angle - MathUtils.PI / 2.f);
				wy = -100 * MathUtils.sin(angle - MathUtils.PI / 2.f);

			} else if (Gdx.input.isKeyPressed(Keys.D)) {
				// Move clockwise around planet
				wx = 100 * MathUtils.cos(angle - MathUtils.PI / 2.f);
				wy = 100 * MathUtils.sin(angle - MathUtils.PI / 2.f);
			} else {
				// Bly stil

				wx = wy = 0;
			}

			if (Gdx.input.isKeyPressed(Keys.SPACE) && onGround) {
				// Jump!

				// Works out which direction is up and shoots the player there
				float jx = 10000 * MathUtils.cos(angle);
				float jy = 10000 * MathUtils.sin(angle);

				dx += jx * dt;
				dy += jy * dt;

				onGround = false;
			}

			x += dx * dt;
			y += dy * dt;

			x += wx * dt;
			y += wy * dt;

			sprite.setRotation(angle * MathUtils.radiansToDegrees - 90);

			bounds.setPosition(x - 10, y - 10);

			sprite.setPosition(x - 10, y - 10);

			// AIMING
			ax = Gdx.input.getX();
			ay = Gdx.input.getY();

			a = -MathUtils.atan2(ay - (Gdx.graphics.getHeight() / 2), ax - (Gdx.graphics.getWidth() / 2)) + angle
					- MathUtils.PI / 2;

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

	public void draw(SpriteBatch batch) {
		if (!controllingShip) {
			sprite.draw(batch);
		}
	}

	public void debugDraw(ShapeRenderer sr) {
		//sr.setColor(Color.BLUE);
		//sr.line(x, y, x + 100 * MathUtils.cos(angle), y + 100 * MathUtils.sin(angle));
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getFX() {
		return controllingShip ? ship.getX() : x;
	}

	public float getFY() {
		return controllingShip ? ship.getY() : y;
	}

	public float getDa() {
		return da;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void die(String string) {

	}

	public float getAngle() {
		return controllingShip ? ship.getAngle() : (angle - MathUtils.PI / 2) % MathUtils.PI2 ;
	}

	public float getDX() {
		return dx;
	}
	
	public float getDY() {
		return dy;
	}

}
