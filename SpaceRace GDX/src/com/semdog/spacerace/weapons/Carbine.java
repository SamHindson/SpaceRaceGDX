package com.semdog.spacerace.weapons;

import com.semdog.spacerace.universe.Universe;

public class Carbine extends Weapon {

	public Carbine() {
		super("Carbine", 500, false, 0.5f, 50, "carbine");
	}

	@Override
	protected void fire() {
		super.fire();
		Universe.currentUniverse.playSound("carbine", owner.getX(), owner.getY(), 0.3f);
	}
}
