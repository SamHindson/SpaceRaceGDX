package com.semdog.spacerace.weapons;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.graphics.Colors;

public class SMG extends Weapon {
    public SMG() {
        super("SMG", 128, true, 0.05f, 7, "smg", 0.003f);
    }

    @Override
    protected void fire() {
        super.fire();
    }

	@Override
	public Color getColor() {
		return Colors.V_SMGAMMO;
	}
}