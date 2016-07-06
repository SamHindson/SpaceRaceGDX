package com.semdog.spacerace.collectables;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.weapons.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by Sam on 2016/07/04.
 */
public class WeaponPickup extends Collectible {
    static final HashMap<String, Class> weaponTypes;

    static {
        weaponTypes = new HashMap<>();
        weaponTypes.put("smg", SMG.class);
        weaponTypes.put("carbine", Carbine.class);
        weaponTypes.put("shotgun", Shotgun.class);
        weaponTypes.put("rocketlauncher", RocketLauncher.class);
    }

    private Class weaponType;

    public WeaponPickup(float h, float a, String type) {
        super(h, a, 10, 10, type, "player");
        this.weaponType = weaponTypes.get(type);
    }

    @Override
    protected void setEnvironment(Planet planet) {
        super.setEnvironment(planet);
    }

    @Override
    public void update(float dt, Array<Collideable> collideables) {
        super.update(dt, collideables);
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    @Override
    public void get(Collideable collideable) {
        Universe.currentUniverse.playUISound("weaponget");
        try {
            ((Player) collideable).setWeapon((Weapon) weaponType.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
