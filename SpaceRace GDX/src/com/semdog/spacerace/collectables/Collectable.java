package com.semdog.spacerace.collectables;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

public abstract class Collectable {
	protected String target;
	protected float x, y, width, height;
	protected Rectangle bounds;
	protected boolean enabled = true;
	
	protected Sprite sprite;
	
	protected Planet environment;
	protected float bob;
	protected float originalHeight;
	protected float angle;
	
	public Collectable(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		bounds = new Rectangle(x, y, width, height);
	}
	
	public void setEnvironment(Planet planet) {
		environment = planet;
		angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());
		originalHeight = Vector2.dst(x, y, planet.getX(), planet.getY());
		sprite.setRotation(angle * MathUtils.radiansToDegrees);
	}
	
	public void update(Array<Collideable> collideables, float dt) {
		bob = MathUtils.sin(Universe.currentUniverse.getAge()) * 3;
		x = environment.getX() + (originalHeight + bob) * MathUtils.cos(angle);
		y = environment.getY() + (originalHeight + bob) * MathUtils.sin(angle);
		sprite.setOriginCenter();
		sprite.setPosition(x, y);
		
		for(int q = 0; q < collideables.size; q++) {
			Collideable collideable = collideables.get(q);
			if(collideable.getType().equals(target) && collideable.canCollect(this)) {
				if(collideable.getBounds().contains(bounds)) {
					get(collideable);
					collideable.collectCollectible(this);
					enabled = false;
					Universe.currentUniverse.killCollectible(this);
				}
			}
		}
	}
	
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public abstract void get(Collideable collideable);

	public void reposition(Array<Planet> planets) {
		for(Planet planet : planets) {
			if(Vector2.dst(x, y, planet.getX(), planet.getY()) < planet.getSOI()) {
				setEnvironment(planet);
				return;
			}
		}
	}
}
