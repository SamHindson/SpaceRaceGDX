package com.semdog.spacerace.weapons;

import com.semdog.spacerace.universe.Universe;

public class SMG extends Weapon {

    public SMG() {
        super("SMG", 128, true, 0.05f, 7, "smg");
    }

    @Override
    protected void fire() {
        super.fire();
        Universe.currentUniverse.playSound("smg", owner.getX(), owner.getY(), 0.3f);
    }
}