package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.players.Player;

public class Grenade extends Mass {
	
	boolean exploded = false;
	
	private Sprite sprite;

	public Grenade(float x, float y, float dx, float dy, float mass, Planet environment) {
		super(x, y, dx, dy, mass, environment);
		sprite = new Sprite(Art.get("grenade"));
		sprite.setSize(5, 5);
		sprite.setOriginCenter();
		
		bounds = new Rectangle(x, y, 5, 5);
	}
	
	@Override
	public void update(float dt, Array<Planet> gravitySources) {
		super.update(dt, gravitySources);
		sprite.rotate(dt * 2000);
		sprite.setPosition(x, y);
		bounds.set(x, y, 5, 5);
	}
	
	@Override
	public void debugRender(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	@Override
	protected void handleCollision(float speed) {
		if(!exploded) {
			exploded = true;
			universe.addExplosion(x, y, 3000);
			universe.killMass(this);
		}
	}
	
	@Override
	public void checkCollisions(Player... s) {
		for(Player player : s) {
			if(bounds.contains(player.getBounds())) {
				exploded = true;
				universe.addExplosion(x, y, 3000);
				universe.killMass(this);
				System.out.println("HIT A PLAYER");
			}
		}
	}

}
