package com.semdog.spacerace.misc;

import com.badlogic.gdx.math.MathUtils;

/**
 * A class which contains useful methods. I plan on using this class for more
 * than one method.
 * 
 * @author Sam
 */

public class Tools {
	/**
	 * Given an array of something, this method returns a random entry of that
	 * array.
	 * 
	 * @param objects
	 * @return the chosen one
	 */
	public static Object decide(Object... objects) {
		return objects[MathUtils.random(objects.length - 1)];
	}
}
