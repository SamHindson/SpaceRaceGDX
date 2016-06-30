package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.graphics.Colors;

public class Shotgun extends Weapon {

	public Shotgun() {
		super("shotgun", 24 * 6, false, 0.5f, 8, "shotgun", 0.03f);
	}

	@Override
	public Color getColor() {
		return Colors.V_SHOTGUNAMMO;
	}

	@Override
	protected void fire() {
		for (int f = 0; f < 6; f++) {
			super.fire();
		}
	}
}
