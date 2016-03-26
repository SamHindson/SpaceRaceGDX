package com.semdog.spacerace;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GameLoader {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1600;
		config.height = 900;
		config.title = "SPACERACE!";
		config.samples = 4;
		config.resizable = false;
		new LwjglApplication(new RaceGame(), config);
	}
}
