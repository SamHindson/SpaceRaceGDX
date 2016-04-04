package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.players.Player;

public abstract class Mass {
	protected float x, y, dx, dy, mass, angle;
	protected boolean onSurface, alive = true;
	protected Planet environment;

	protected static Texture texture;
	protected static Universe universe;

	protected Rectangle bounds;

	protected int currentHealth, maxHealth;

	public static void initiate(Universe _universe) {
		universe = _universe;
	}

	static {
		texture = new Texture(Gdx.files.internal("assets/test.png"));
	}

	public Mass(float x, float y, float dx, float dy, float mass, float width, float height, Planet environment) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.mass = mass;
		this.environment = environment;

		universe.addMass(this);

		if (width == 0 || height == 0) {
			Gdx.app.error("Mass", "Warning! Mass created with a zero width or height. What the hell mate?");
		}

		bounds = new Rectangle(x, y, width, height);
	}

	public Mass(float x, float y, float dx, float dy, float mass, Planet environment) {
		this(x, y, dx, dy, mass, 0, 0, environment);
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		currentHealth = maxHealth;
	}

	public void doDamage(int amount) {
		if (currentHealth <= amount) {
			currentHealth = 0;
			alive = false;
			die();
		} else {
			currentHealth -= amount;
		}
	}

	public void update(float dt, Array<Planet> gravitySources) {
		angle = MathUtils.atan2(y - environment.getY(), x - environment.getX());

		for (int i = 0; i < gravitySources.size; i++) {
			Planet planet = gravitySources.get(i);
			if (inRange(planet) && !onSurface) {

				if (!environment.equals(planet)) {
					environment = planet;
				}

				float force = (float) (Universe.GRAVITY * mass * planet.getMass() / Math.pow(distance(planet), 2));
				float ax = -dt * force * MathUtils.cos(angle);
				float ay = -dt * force * MathUtils.sin(angle);

				ax /= mass;
				ay /= mass;

				dx += ax * dt * 100;
				dy += ay * dt * 100;
			}
		}

		x += dx * dt;
		y += dy * dt;

		bounds.setPosition(x - getWidth() / 2, y - getHeight() / 2);
	}
	
	public void checkState() {
		onSurface(environment);
	}

	protected boolean onSurface(Planet planet) {
		if (!onSurface) {
			if (distance(planet) <= planet.getRadius() + getHeight() / 2.f) {
				float speed = Vector2.dst(0, 0, dx, dy);
				handleCollision(speed);
			} else {
				onSurface = false;
			}
		} else {
			if (distance(planet) >= planet.getRadius() + getHeight() / 2.f) {
				onSurface = false;
			}
		}
		return onSurface;
	}

	protected void handleCollision(float speed) {
		onSurface = true;
		dx = dy = 0;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public boolean isAlive() {
		return alive;
	}
	
	protected void die() {
		Gdx.app.log("Mass", "A mass was just told to die.");
	}

	protected abstract float getWidth();

	protected abstract float getHeight();

	protected boolean inRange(Planet planet) {
		return distance(planet) < planet.getSOI();
	}

	protected float distance(Planet planet) {
		return Vector2.dst(x, y, planet.getX(), planet.getY());
	}

	public void debugRender(ShapeRenderer renderer) {
		/*renderer.setColor(Color.WHITE);
		renderer.set(ShapeType.Filled);
		renderer.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		renderer.rect(600, 0, 50, 50);*/
	}

	public void render(SpriteBatch batch) {

	}

	public void checkCollisions(Player... s) {

	}
}
