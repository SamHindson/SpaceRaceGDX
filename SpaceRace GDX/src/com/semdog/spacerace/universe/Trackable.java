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
    /**
     * The color of the entity in the map view. Different for each one
     */
    Color getGizmoColor();

    /**
     * What the entity's display name will be
     */
    String getGizmoLabel();

    /** Where the entity is */
    Vector2 getPosition();
}
