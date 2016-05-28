package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.universe.Universe;

public class Explosion extends Effect {
	private ParticleEffect effect;

    public Explosion(float x, float y) {
        this.x = x;
        this.y = y;

		effect = new ParticleEffect();
        effect.load(Gdx.files.internal("assets/effects/explosion3.p"), Gdx.files.internal("assets/effects"));

		effect.setPosition(x, y);
		effect.start();

		Universe.currentUniverse.playSound("explosion" + Tools.decide("1", "2", "3"), x, y, 0.2f);
	}

	public void render(SpriteBatch batch) {
		effect.draw(batch);
	}

	public void update(float dt) {
        effect.update(dt);
        effect.setPosition(x, y);
    }

	@Override
	public boolean isAlive() {
		return !effect.isComplete();
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}
