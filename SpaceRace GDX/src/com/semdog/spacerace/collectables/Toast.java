package com.semdog.spacerace.collectables;

import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.vehicles.Ship;

/**
 * A collectible which serves no other purpose than to lure the player across
 * the solar system.
 * 
 * @author Sam
 */

public class Toast extends Collectible {
	public Toast(float x, float y) {
		super(x, y, 20, 20, "toast", 0x11);
	}

	@Override
	public void get(Collideable collideable) {
		Universe.currentUniverse.playUISound("toastget");
		
		if(collideable instanceof Player)
			((Player) collideable).addToast();
		else
			((Ship) collideable).addToast();
	}
}
