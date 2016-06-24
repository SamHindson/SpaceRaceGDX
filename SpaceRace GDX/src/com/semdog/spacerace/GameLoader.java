package com.semdog.spacerace;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GameLoader {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1800;
        config.height = config.width * 9 / 16;
        config.title = "SPACERACE!";
		config.samples = 4;
		config.resizable = false;
		new LwjglApplication(new RaceGame(), config);
	}
}