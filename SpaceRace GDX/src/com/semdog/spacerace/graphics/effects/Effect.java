package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class Effect implements Disposable {
    protected float x;
	protected float y;
	
	public abstract void update(float dt);
	public abstract void render(SpriteBatch batch);
	
	public abstract boolean isAlive();
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}


}
