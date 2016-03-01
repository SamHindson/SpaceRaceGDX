package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;

public class Grenade extends Mass {
	
	boolean exploded = false;
	
	private Sprite sprite;

	public Grenade(float x, float y, float dx, float dy, float mass, Planet environment) {
		super(x, y, dx, dy, mass, environment);
		sprite = new Sprite(Art.get("grenade"));
		sprite.setSize(5, 5);
		sprite.setOriginCenter();
	}
	
	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		sprite.rotate(dt * 2000);
		sprite.setPosition(x, y);
	}
	
	@Override
	public void debugRender(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	@Override
	protected void handleCollision(float speed) {
		if(!exploded) {
			exploded = true;
			universe.addExplosion(x, y, 100);
			universe.killMass(this);
		}
	}

}
