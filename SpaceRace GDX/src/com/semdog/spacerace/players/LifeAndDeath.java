package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

public class LifeAndDeath {
	private static String[] condolences;
	
	static {
		FileHandle file = Gdx.files.internal("assets/text/condolences.sr");
		String full = file.readString();
		condolences = full.split("\n");
	}
	
	public static String getRandomCondolence() {
		return condolences[MathUtils.random(condolences.length - 1)];
	}
}
