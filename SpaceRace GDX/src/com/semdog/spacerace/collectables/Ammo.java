package com.semdog.spacerace.collectables;

import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;

/**
 * Created by Sam on 2016/07/09.
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
