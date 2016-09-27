package com.semdog.spacerace.collectables;

import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;

/**
 * A collectible which replenishes the player's health.
 */

public class Health extends Collectible {

    public Health(float h, float a) {
        super(h, a, 10, 10, "health", 0x01);
    }

    @Override
    public void get(Collideable collideable) {
        Universe.currentUniverse.playUISound("healthget");
        ((Player) collideable).replenishHealth();
    }
}
