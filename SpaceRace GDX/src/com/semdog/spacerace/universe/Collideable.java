package com.semdog.spacerace.universe;

import com.badlogic.gdx.math.Rectangle;
import com.semdog.spacerace.collectables.Collectible;

public interface Collideable {
    void collectCollectible(Collectible collectible);

    Rectangle getBounds();

    boolean canCollect(Collectible collectible);

    String getType();
}