package com.semdog.spacerace.universe;

import com.badlogic.gdx.math.Rectangle;
import com.semdog.spacerace.collectables.Collectible;

/**
 * An interface that allows certain objects to collide with the ingame Collectibles.
 *
 * @author Sam
 */

public interface Collideable {
    void collectCollectible(Collectible collectible);

    Rectangle getCollisionBounds();

    boolean canCollect(Collectible collectible); // Check whether it can pick up a certain collectible

    int getType(); // 0x01 if player, 0x10 if spaceship
}