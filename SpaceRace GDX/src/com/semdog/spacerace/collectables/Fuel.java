package com.semdog.spacerace.collectables;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.vehicles.Ship;

/**
 * A pickup that refuels a ship. It also kills a player if they collect it
 * naked. Rocket fuel is not safe to drink.
 *
 * @author Sam
 */

public class Fuel extends Collectible {
    public Fuel(float h, float a) {
        super(h, a, 10, 10, "fuel", 0x11);
    }

    //  If the Collideable getting the fuel is a ship, replenish its fuel reserves.
    //  If it is a player, kill them --- rocket fuel is not to be drunk.
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

    // For some reason, the accent color of the Fuel's texture is black. Having
    // black sparkles against a black background is not magical at all, so we
    // rather make them red.
    @Override
    public Color getGizmoColor() {
        return Colors.P_RED;
    }
}
