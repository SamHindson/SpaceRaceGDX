package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.universe.Universe;

/**
 * A weapon which launches rockets.
 * See: Rocket.java
 */

public class RocketLauncher extends Weapon {

    public RocketLauncher() {
        super("rocketlauncher", 5, false, 0.5f, 0, "rocketlaunch", 0);
    }

    @Override
    public Color getColor() {
        return Colors.V_ROCKETAMMO;
    }

    @Override
    protected void fire() {
        if (ammoleft > 0) {
            owner.kickBack(-aimAngle - MathUtils.PI / 2.f, 200);
            ammoleft--;
            Universe.currentUniverse.playSound(fireSound, owner.getX(), owner.getY(), 1);
            new Rocket(owner.getWeaponX(), owner.getWeaponY(), aimAngle, owner);
        }
    }
}
