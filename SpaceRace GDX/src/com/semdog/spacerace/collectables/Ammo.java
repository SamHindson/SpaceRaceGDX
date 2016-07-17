package com.semdog.spacerace.collectables;

import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;

/**
 * A collectible that replenishes the player's ammunition supplies.
 *
 * @author Sam
 */

public class Ammo extends Collectible {
    public Ammo(float h, float a) {
        super(h, a, 10, 10, "fuel", 0x01);
    }

    @Override
    protected void get(Collideable collideable) {
        Universe.currentUniverse.playUISound("ammoget");
        ((Player) collideable).replenishAmmo();
    }
}
