package com.semdog.spacerace.collectables;

import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.vehicles.Ship;

/**
 * Created by Sam on 2016/07/09.
 */
public class Fuel extends Collectible {
    public Fuel(float h, float a) {
        super(h, a, 10, 10, "fuel", 0x11);
    }

    @Override
    protected void get(Collideable collideable) {
        if (collideable instanceof Ship) {
            Universe.currentUniverse.playUISound("fuelget");
            ((Ship) collideable).replenishFuel();
        } else {
            Universe.currentUniverse.playUISound("fueldrink");
            ((Player) collideable).doDamage(1000, DamageCause.DRINKINGFUEL);
        }
    }
}
