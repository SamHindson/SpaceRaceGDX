package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.players.DeathCause;
import com.semdog.spacerace.players.Player;

public abstract class Mass {
	protected static Texture texture;

	static {
		texture = new Texture(Gdx.files.internal("assets/test.png"));
	}

	protected float x, y, dx, dy, mass, angle;
	protected boolean onSurface, alive = true;
	protected Planet environment;
	protected boolean shouldCollide;
	protected Rectangle bounds;
    protected float currentHealth, maxHealth;

	protected boolean loaded = false;

	public Mass(float x, float y, float dx, float dy, float mass, float width, float height, Planet environment) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.mass = mass;
		this.environment = environment;

		shouldCollide = true;

		Universe.currentUniverse.addMass(this);

		if (width == 0 || height == 0) {
			Gdx.app.error("Mass", "Warning! Mass created with a zero width or height. What the hell mate?");
		}

		bounds = new Rectangle(x, y, width, height);
	}

	public Mass(float x, float y, float dx, float dy, float mass, Planet environment) {
		this(x, y, dx, dy, mass, 0, 0, environment);
	}

	public Mass(float x, float y, float dx, float dy, float mass, float width, float height) {
		this(x, y, dx, dy, mass, width, height, null);
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		currentHealth = maxHealth;
	}

    public void doDamage(float amount, DeathCause reason) {
        if (currentHealth <= amount) {
            currentHealth = 0;
			alive = false;
			die(reason);
		} else {
			currentHealth -= amount;
		}
	}

	public void update(float dt, Array<Planet> gravitySources) {
		if (environment != null) {
			angle = MathUtils.atan2(y - environment.getY(), x - environment.getX());
		}

		for (int i = 0; i < gravitySources.size; i++) {
			Planet planet = gravitySources.get(i);
			if (inRange(planet) && !onSurface) {

				// if (!environment.equals(planet)) {
				setEnvironment(planet);
				// }

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

	protected void setEnvironment(Planet planet) {
		environment = planet;
	}

	protected abstract float getImpactThreshold();

	/**
	 * A method that does some useful stuff regarding the mass, like checking
	 * whether it's on the planet surface and whether it's got its stuff loaded
	 */
	public void checkState() {
		onSurface(environment);

		if (!loaded)
			load();
	}

	protected boolean onSurface(Planet planet) {
		if (environment != null) {
			if (!onSurface) {
				if (distance(planet) <= planet.getRadius() + getHeight() / 2.f) {
					float speed = Vector2.dst(0, 0, dx, dy);
					handlePlanetCollision(speed, true);
				} else {
					onSurface = false;
				}
			} else {
				if (distance(planet) >= planet.getRadius() + getHeight() / 2.f) {
					onSurface = false;
				}
			}
		}
		return onSurface;
	}

	protected void handlePlanetCollision(float speed, boolean withPlanet) {
		onSurface = true;
		dx = dy = 0;
	}

	protected void load() {
		loaded = true;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public boolean isAlive() {
		return alive;
	}

	protected void die(DeathCause reason) {
		alive = false;
	}

	protected abstract float getWidth();

	protected abstract float getHeight();

	protected float getAverageRadius() {
		return (getWidth() + getHeight()) / 4.f;
	}

	protected boolean inRange(Planet planet) {
		return distance(planet) < planet.getSOI();
	}

	protected float distance(Planet planet) {
		return Vector2.dst(x, y, planet.getX(), planet.getY());
	}

	public void debugRender(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.set(ShapeType.Filled);
		renderer.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public void render(SpriteBatch batch) {

	}

	public void checkCollisions(Array<Mass> masses) {
		for (int u = 0; u < masses.size; u++) {
			Mass m = masses.get(u);

			if (!m.equals(this)) {
				if (Intersector.overlaps(getBounds(), m.getBounds()) && m.shouldCollide && shouldCollide) {
					float v = Vector2.len(dx - m.getDx(), dy - m.getDy());
					float d = Vector2.dst(x, y, m.getX(), m.getY());
					float a1 = MathUtils.atan2(m.getY() - getY(), m.getX() - getX());
					float a2 = -a1;
					float r = (getRadius(a1) + m.getRadius(a2));
					float i = (d - r) / 2.f;

					if (v > getImpactThreshold()) {
						die(DeathCause.SHIP);
					}

					x += i * MathUtils.cos(a1);
					y += i * MathUtils.sin(a1);

					float p1 = mass * getVelocity();
					float p2 = m.mass * m.getVelocity();

					float pf = (p1 + p2) / 2.f;

					dx += (pf / mass) * -MathUtils.cos(a1);
					dy += (pf / mass) * -MathUtils.sin(a1);
				}
			}
		}
	}

	public float getRadius(float angle) {
		float ar = getAverageRadius();
		return ar * (float) Math.min(Math.abs(1.f / (Math.cos(angle))), Math.abs(1.f / (Math.sin(angle))));
	}

	public float getVelocity() {
		return Vector2.dst(0, 0, dx, dy);
	}

	protected abstract void hitPlayer(Player player);

	public void checkPlayerCollision(Player player) {
		if (Intersector.overlaps(getBounds(), player.getBounds())) {
			if (getVelocity() - player.getVelocity() > 50) {
				hitPlayer(player);
                Universe.currentUniverse.playSound("playerhit" + Tools.decide(1, 2, 3, 4, 5), x, y, 0);
            }
        }
	}
}
