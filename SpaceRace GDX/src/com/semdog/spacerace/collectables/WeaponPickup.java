package com.semdog.spacerace.collectables;

import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.weapons.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * The WeaponPickup, when collected, gives the player a gun. The type of gun is
 * intrinsic to the pickup.
 *
 * @author Sam
 */

public class WeaponPickup extends Collectible {
    // To be able to instantiate weapons on the run (and without having to hard
    // code much), this hashmap stores the weapon types needed.
    private static final HashMap<String, Class<?>> weaponTypes;

    static {
        weaponTypes = new HashMap<>();
        weaponTypes.put("smg", SMG.class);
        weaponTypes.put("carbine", Carbine.class);
        weaponTypes.put("shotgun", Shotgun.class);
        weaponTypes.put("rocketlauncher", RocketLauncher.class);
    }

    //	Stores what kind of weapon it will give the player.
    private Class<?> weaponType;

    public WeaponPickup(float h, float a, String type) {
        super(h, a, 10, 10, type, 0x01);
        this.weaponType = weaponTypes.get(type);
    }

    /**
     * Gives the player the designated weapon.
     */
    @Override
    public void get(Collideable collideable) {
        Universe.currentUniverse.playUISound("weaponget");
        try {
            ((Player) collideable).setWeapon((Weapon) weaponType.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
