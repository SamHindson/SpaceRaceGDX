package com.semdog.spacerace.universe;

import com.badlogic.gdx.math.Rectangle;
import com.semdog.spacerace.collectables.Collectable;

public interface Collideable {
	public void collectCollectible(Collectable collectable);
	public Rectangle getBounds();
	public boolean canCollect(Collectable collectable);
	public String getType();
}