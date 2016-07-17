package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.graphics.Colors;

/**
 * A weapon which fires single bullets which have decent damage points.
 * 
 * @author Sam
 */

public class Carbine extends Weapon {
	public Carbine() {
		super("Carbine", 32, false, 0.05f, 27, "carbine", 0.05f);
	}

	@Override
	public Color getColor() {
		return Colors.V_CARBINEAMMO;
	}
}
