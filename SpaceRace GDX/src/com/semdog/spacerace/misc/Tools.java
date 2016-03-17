package com.semdog.spacerace.misc;

import com.badlogic.gdx.math.MathUtils;

public class Tools {
	public static Object decide(Object...objects) {
		return objects[MathUtils.random(objects.length - 1)];
	}
}
