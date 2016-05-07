package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DeathCause;
import com.semdog.spacerace.players.Player;

/***
 * Kabuum
 */

public class Grenade extends Mass {

	private boolean exploded = false;

	private Sprite sprite;

    private float timer = 0;
    private float beepTimer = 0;

    private ParticleEffect trail;

	public Grenade(float x, float y, float dx, float dy, float mass, Planet environment) {
		super(x, y, dx, dy, 2, 2, mass, environment);
		sprite = new Sprite(Art.get("grenade"));
		sprite.setSize(2, 2);
		sprite.setOriginCenter();

		bounds = new Rectangle(x, y, 1, 1);

        trail = new ParticleEffect();
        trail.load(Gdx.files.internal("assets/effects/grenadetrail.p"), Gdx.files.internal("assets/effects"));
        trail.setPosition(x, y);
    }

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		if(sprite == null && alive) {
			Gdx.app.error("Grenade", "BIG MISTAKE!!!");
		}

        trail.setPosition(x, y);
        trail.update(dt);

        beepTimer += dt;
        timer += dt;

        if (beepTimer > 0.1f) {
            beepTimer = 0;
            Universe.currentUniverse.playSound("neet", x, y, -0.9f);
        }

		sprite.rotate(dt * 2000);
		sprite.setPosition(x, y);
		bounds.set(x, y, 1, 1);
	}


    @Override
    protected float getImpactThreshhold() {
		return 0;
	}

	public void render(SpriteBatch batch) {
        trail.draw(batch);
        sprite.draw(batch);

        if (timer > 5) {
            explode();
        }
    }

	@Override
    protected void handlePlanetCollision(float speed, boolean e) {
        if (!exploded) {
            float surfaceAngle = MathUtils.atan2(y - environment.getY(), x - environment.getX());
            float velocityAngle = MathUtils.atan2(dy, dx);
            float impactAngle = -(MathUtils.PI / 2.f + surfaceAngle) - velocityAngle;
            dx *= MathUtils.cos(impactAngle);
            dy *= MathUtils.sin(impactAngle);
            x += dx * 0.016f;
            y += dy * 0.016f;
        }
    }

	@Override
	protected void hitPlayer(Player player) {
		if (!exploded) {
			Universe.currentUniverse.playerKilled(player, DeathCause.EXPLOSION);
			explode();
		}
	}

	private void explode() {
		exploded = true;
        Universe.currentUniverse.addEffect(new Explosion(x, y, dx, dy));
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
}
