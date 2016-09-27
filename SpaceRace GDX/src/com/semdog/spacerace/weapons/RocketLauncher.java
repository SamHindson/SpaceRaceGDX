package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.net.entities.MassSpawnRequest;
import com.semdog.spacerace.universe.MultiplayerUniverse;
import com.semdog.spacerace.universe.Universe;

/**
 * A weapon which launches rockets.
 * See: Rocket.java
 *
 * @author Sam
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

            if (Universe.currentUniverse instanceof MultiplayerUniverse)
                ((MultiplayerUniverse) Universe.currentUniverse).requestMass(new MassSpawnRequest(MassSpawnRequest.ROCKET, owner.getWeaponX(), owner.getWeaponY(), 700 * MathUtils.cos(aimAngle), 700 * MathUtils.sin(aimAngle), 5));
            else
                new Rocket(owner.getWeaponX(), owner.getWeaponY(), aimAngle, 420);
        }
    }
}
