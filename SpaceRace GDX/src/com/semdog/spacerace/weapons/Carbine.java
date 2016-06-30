package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.universe.Universe;

public class Carbine extends Weapon {

	public Carbine() {
        super("Carbine", 32, false, 0.05f, 27, "carbine");
    }

	@Override
	protected void fire() {
		super.fire();
		Universe.currentUniverse.playSound("carbine", owner.getX(), owner.getY(), 0.3f);
	}

	@Override
	public Color getColor() {
		return Colors.V_CARBINEAMMO;
	}
}
