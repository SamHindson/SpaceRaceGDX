package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DustPuff extends Effect {
	private ParticleEffect effect;
	
	public DustPuff(float x, float y, Color color) {
		this.x = x;
		this.y = y;
		
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("assets/effects/dustpuff.p"), Gdx.files.internal("assets/effects"));
		effect.getEmitters().get(0).getTint().setColors(new float[] {
				color.r, color.g, color.b, 1, color.r, color.g, color.b, 1
		});
		effect.setPosition(x, y);
		System.out.println("Puff created.");
		effect.start();
	}
	
	public void render(SpriteBatch batch) {
		effect.draw(batch);
	}
	
	public void update(float dt) {
		effect.update(dt);
	}

	@Override
	public boolean isAlive() {
		return !effect.isComplete();
	}
}
