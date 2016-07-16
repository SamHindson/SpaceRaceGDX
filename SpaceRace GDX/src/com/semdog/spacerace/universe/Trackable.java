package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * An interface which allows the flight computer to track and draw certain
 * objects.
 * 
 * @author Sam
 */

public interface Trackable {
	Color getGizmoColor();

	String getGizmoLabel();

	Vector2 getPosition();
}
