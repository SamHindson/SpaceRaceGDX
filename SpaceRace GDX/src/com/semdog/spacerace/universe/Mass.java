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
import com.semdog.spacerace.misc.OrbitalHelper;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;

public abstract class Mass implements Goalobject {
	protected static Texture texture;

	static {
		texture = new Texture(Gdx.files.internal("assets/test.png"));
	}

	protected Vector2 position, velocity;
	protected float mass, angle;
	protected float width, height;
	protected boolean onGround, alive = true;
	protected Planet environment;
	protected boolean shouldCollide;
	protected Rectangle bounds;
	protected float currentHealth, maxHealth;

	protected boolean loaded = false;

	protected String id;

	public Mass(float x, float y, float dx, float dy, float mass, float width, float height, Planet environment,
			String id) {
		position = new Vector2(x, y);
		velocity = new Vector2(dx, dy);
		this.mass = mass;
		this.environment = environment;
		this.id = id;

		this.width = width;
		this.height = height;

		shouldCollide = true;

		Universe.currentUniverse.addMass(this);

		if (width == 0 || height == 0) {
			Gdx.app.error("Mass", "Warning! Mass created with a zero width or height. What the hell mate?");
		}

		bounds = new Rectangle(x, y, width, height);
	}

	public Mass(float x, float y, float dx, float dy, float mass, Planet environment, String id) {
		this(x, y, dx, dy, mass, 0, 0, environment, id);
	}

	public Mass(float x, float y, float dx, float dy, float mass, float width, float height, String id) {
		this(x, y, dx, dy, mass, width, height, null, id);
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		currentHealth = maxHealth;
	}

	public boolean isOnGround() {
		return Vector2.dst(position.x, position.y, environment.getX(), environment.getY()) <= environment.getRadius() + 5;
	}

	public void doDamage(float amount, DamageCause reason) {
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
			angle = MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());
		}

		for (int i = 0; i < gravitySources.size; i++) {
			Planet planet = gravitySources.get(i);
			if (inRange(planet) && !onGround) {
				setEnvironment(planet);

				float force = (float) (Universe.GRAVITY * planet.getMass() / Math.pow(distance(planet), 2));
				float ax = -force * MathUtils.cos(angle);
				float ay = -force * MathUtils.sin(angle);

				velocity.x += ax * dt;
				velocity.y += ay * dt;
			}
		}

		position.x += velocity.x * dt;
		position.y += velocity.y * dt;

		bounds.setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);
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
			if (!onGround) {
				if (distance(planet) <= planet.getRadius() + getHeight() / 2.f) {
					float speed = Vector2.dst(0, 0, velocity.x, velocity.y);
					handlePlanetCollision(speed, true);
				} else {
					onGround = false;
				}
			} else {
				if (distance(planet) >= planet.getRadius() + getHeight() / 2.f) {
					onGround = false;
				}
			}
		}
		return onGround;
	}

	protected void handlePlanetCollision(float speed, boolean withPlanet) {
		if (speed > getImpactThreshold()) {
			die(DamageCause.PLANET);
		}
		onGround = true;
		velocity.x = velocity.y = 0;
	}

	protected void load() {
		loaded = true;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public float getDx() {
		return velocity.x;
	}

	public float getDy() {
		return velocity.y;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public boolean isAlive() {
		return alive;
	}

	protected void die(DamageCause reason) {
		alive = false;
	}

	protected float getWidth() {
		return width;
	}

	protected float getHeight() {
		return height;
	}

	protected float getAverageRadius() {
		return (getWidth() + getHeight()) / 4.f;
	}

	protected boolean inRange(Planet planet) {
		return distance(planet) < planet.getSOI();
	}

	protected float distance(Planet planet) {
		return Vector2.dst(position.x, position.y, planet.getX(), planet.getY());
	}

	public void debugRender(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.set(ShapeType.Line);
		renderer.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public void render(SpriteBatch batch) {

	}

	public void checkCollisions(Array<Mass> masses) {
		for (int u = 0; u < masses.size; u++) {
			Mass m = masses.get(u);

			if (!m.equals(this)) {
				if (Intersector.overlaps(getBounds(), m.getBounds()) && m.shouldCollide && shouldCollide) {
					float v = Vector2.len(velocity.x - m.getDx(), velocity.y - m.getDy());
					float d = Vector2.dst(position.x, position.y, m.getX(), m.getY());
					float a1 = MathUtils.atan2(m.getY() - getY(), m.getX() - getX());
					float a2 = -a1;
					float r = (getRadius(a1) + m.getRadius(a2));
					float i = (d - r) / 2.f;

					handleMassCollision();

					if (v > getImpactThreshold()) {
						die(DamageCause.DEBRIS);
					}

					position.x += i * MathUtils.cos(a1);
					position.y += i * MathUtils.sin(a1);

					float p1 = mass * getVelocity().len();
					float p2 = m.mass * m.getVelocity().len();

					float pf = (p1 + p2) / 2.f;

					velocity.x += (pf / mass) * -MathUtils.cos(a1);
					velocity.x += (pf / mass) * -MathUtils.sin(a1);
				}
			}
		}
	}

	protected void handleMassCollision() {

	}

	public float getRadius(float angle) {
		float ar = getAverageRadius();
		return ar * (float) Math.min(Math.abs(1.f / (Math.cos(angle))), Math.abs(1.f / (Math.sin(angle))));
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	protected abstract void hitPlayer(Player player);

	public void checkPlayerCollision(Player player) {
		if (Intersector.overlaps(getBounds(), player.getBounds())) {
			if (getVelocity().len() - player.getVelocity() > 50) {
				hitPlayer(player);
				Universe.currentUniverse.playSound("playerhit" + Tools.decide(1, 2, 3, 4, 5), position.x, position.y, 0);
			}
		}
	}

	public void setDx(float dx) {
		velocity.x = dx;
	}

	public void setDy(float dy) {
		velocity.y = dy;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public float getPerigee() {
		return OrbitalHelper.computeOrbit(environment.getPosition(), getPosition(), getVelocity(), environment.getMass())[6];
	}
	
	public float getApogee() {
		return OrbitalHelper.computeOrbit(environment.getPosition(), getPosition(), getVelocity(), environment.getMass())[5];
	}
}
