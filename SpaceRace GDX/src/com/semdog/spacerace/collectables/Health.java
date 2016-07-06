package com.semdog.spacerace.collectables;

import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;

public class Health extends Collectible {

	public Health(float x, float y) {
        super(x, y, 10, 10, "health", "player");
    }

	@Override
	public void get(Collideable collideable) {
		Universe.currentUniverse.playUISound("healthget");
		((Player)collideable).replenishHealth();
	}


}
