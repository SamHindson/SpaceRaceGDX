package com.semdog.spacerace.collectables;

import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Universe;

/**
 * Created by Sam on 2016/07/04.
 */
public class Toast extends Collectible {
    public Toast(float x, float y) {
        super(x, y, 20, 20, "toast", 0x11);
    }

    @Override
    public void get(Collideable collideable) {
        Universe.currentUniverse.playUISound("toastget");
        ((Player) collideable).addToast();
    }
}
