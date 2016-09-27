package com.semdog.spacerace.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * A class which contains useful methods. I plan on using this class for more
 * than two methods.
 *
 * @author Sam
 */

public class Tools {
    /**
     * Given an array of something, this method returns a random entry of that array.
     */
    public static Object decide(Object... objects) {
        return objects[MathUtils.random(objects.length - 1)];
    }

    public static Color darker(Color color) {
        float r = color.r * 0.7f;
        float g = color.g * 0.7f;
        float b = color.b * 0.7f;
        return new Color(r, g, b, 1);
    }

    public static Color lighter(Color color) {
        float r = color.r * 1.2f;
        float g = color.g * 1.2f;
        float b = color.b * 1.2f;
        if (r > 1) r = 1;
        if (g > 1) g = 1;
        if (b > 1) b = 1;
        return new Color(r, g, b, 1);
    }
}
