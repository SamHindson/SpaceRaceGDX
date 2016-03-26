package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Effect {
	protected float x;
	protected float y;
	
	public abstract void update(float dt);
	public abstract void render(SpriteBatch batch);
	
	public abstract boolean isAlive();
}
