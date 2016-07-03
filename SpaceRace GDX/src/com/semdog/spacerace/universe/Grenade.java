package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;

/***
 * Kabuum
 */

public class Grenade extends Mass {

	private boolean exploded = false, bouncing = true;

	private Sprite sprite;

	private float timer = 0;
	private float beepTimer = 0;

	private ParticleEffect trail;

	public Grenade(float x, float y, float dx, float dy, float mass, Planet environment, String id) {
		super(x, y, dx, dy, 2, 2, mass, environment, id);

		bounds = new Rectangle(x, y, 1, 1);

		trail = new ParticleEffect();
		trail.setPosition(x, y);
	}

	@Override
	protected void load() {
		trail.load(Gdx.files.internal("assets/effects/grenadetrail.p"), Gdx.files.internal("assets/effects"));
		sprite = new Sprite(Art.get("grenade"));
		sprite.setSize(2, 2);
		sprite.setOriginCenter();
		super.load();
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);

		if (loaded) {
			trail.setPosition(position.x, position.y);
			trail.update(dt);

			sprite.rotate(dt * 2000);
			sprite.setPosition(position.x, position.y);
		}

		beepTimer += dt;
		timer += dt;

		if (beepTimer > 0.1f) {
			beepTimer = 0;
			Universe.currentUniverse.playSound("neet", position.x, position.y, -0.9f);
		}
		bounds.set(position.x, position.y, 1, 1);
	}

	@Override
	protected void handleMassCollision(Mass mass) {
		Gdx.app.log("Grenade", "Hit a mass!");
		explode();
	}

	@Override
	protected float getImpactThreshold() {
		return 0;
	}

	public void render(SpriteBatch batch) {
		if (loaded) {
			trail.draw(batch);
			sprite.draw(batch);
		}

		if (timer > 5) {
			explode();
		}
	}

	@Override
	protected void handlePlanetCollision(float speed, boolean e) {
		if (!exploded) {
			if (bouncing) {
				float surfaceAngle = MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());
				float velocityAngle = MathUtils.atan2(velocity.y, velocity.x);
				float impactAngle = -(MathUtils.PI / 2.f + surfaceAngle) - velocityAngle;
				velocity.x *= MathUtils.cos(impactAngle);
				velocity.y *= MathUtils.sin(impactAngle);
				position.x += velocity.x * 0.016f;
				position.y += velocity.y * 0.016f;

				if (velocity.len() < 0.5f) {
					bouncing = false;
					velocity.set(Vector2.Zero);
				}
			} else {
				position.set(environment.getX() + environment.getRadius() * MathUtils.cos(angle),
						environment.getY() + environment.getRadius() * MathUtils.sin(angle));
			}
		}
	}

	@Override
	protected void hitPlayer(Player player) {
		if (!exploded) {
			Universe.currentUniverse.playerKilled(player, DamageCause.EXPLOSION);
			explode();
		}
	}

	public void explode() {
		exploded = true;
		Universe.currentUniverse.addEffect(new Explosion(position.x, position.y));
		die(DamageCause.EXPLOSION);
	}

	@Override
	public boolean isAlive() {
		return !exploded;
	}

	@Override
	protected float getWidth() {
		return 1;
	}

	@Override
	protected float getHeight() {
		return 1;
	}

	@Override
	public String getID() {
		return "grenade";
	}

	@Override
	public void die(DamageCause reason) {
		super.die(reason);
	}

    @Override
    public void dispose() {
        trail.dispose();
        sprite.getTexture().dispose();
    }
}