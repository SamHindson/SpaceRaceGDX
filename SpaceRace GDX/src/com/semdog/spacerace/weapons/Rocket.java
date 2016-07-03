package com.semdog.spacerace.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

public class Rocket extends Mass {
	
	private Sprite sprite;
	private ParticleEffect particleEffect;

	public Rocket(float x, float y, float direction, Player owner) {
		super(x + 20 * MathUtils.cos(direction) , y + 20 * MathUtils.sin(direction), 700 * MathUtils.cos(direction),
				700 * MathUtils.sin(direction), 10, owner.getEnvironment(), "rocket");
		width = 3;
		height = 7.5f;
		//gravityEnabled = false;
		sprite = new Sprite(Art.get("rocket"));
		sprite.setOriginCenter();
		sprite.setSize(3, 7.5f);
		
		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("assets/effects/rocketflame.p"), Gdx.files.internal("assets/effects"));
		particleEffect.setPosition(x, y);
		particleEffect.getEmitters().get(0).getAngle().setHigh(direction * MathUtils.radiansToDegrees + 180);
	}

	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		sprite.setPosition(position.x, position.y);
		sprite.setOriginCenter();
		sprite.setRotation(velocity.angle() - 90);

		particleEffect.getEmitters().get(0).getAngle().setHigh(velocity.angle() + 180);
		particleEffect.setPosition(position.x, position.y);
		particleEffect.update(dt);
		
		if(age > 2.5f) {
			explode();
		}
	}

	@Override
	public String getID() {
		return "rocket";
	}
	
	@Override
	public void render(SpriteBatch batch) {
		particleEffect.draw(batch);
		sprite.draw(batch);
	}

	@Override
	protected float getImpactThreshold() {
		return 0;
	}
	
	@Override
	protected void handlePlanetCollision(float speed, boolean withPlanet) {
		super.handlePlanetCollision(speed, withPlanet);
		explode();
	}
	
	public void explode() {
		Universe.currentUniverse.addEffect(new Explosion(position.x, position.y));
	}
	
	@Override
	protected void handleMassCollision(Mass mass) {
		explode();
	}

	@Override
	protected void hitPlayer(Player player) {
		explode();
	}

    @Override
    public void dispose() {
        particleEffect.dispose();
    }
}
