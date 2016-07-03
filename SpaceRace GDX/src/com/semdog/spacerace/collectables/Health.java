package com.semdog.spacerace.collectables;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;

public class Health extends Collectible {

	public Health(float x, float y) {
		super(x, y, 10, 10);
		sprite = new Sprite(Art.get("health"));
		sprite.setOriginCenter();
		sprite.setSize(width, height);
		sprite.setPosition(x, y);
		 
		target = "player";
	}

	@Override
	public void get(Collideable collideable) {
		Universe.currentUniverse.playUISound("healthget");
		((Player)collideable).replenishHealth();
	}


}
