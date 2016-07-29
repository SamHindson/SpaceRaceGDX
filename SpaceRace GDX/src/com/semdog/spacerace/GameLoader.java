package com.semdog.spacerace;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * The entry point of SpaceRace.
 */

public class GameLoader {
    private static final String version = "0.3.5";

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "SpaceRace v" + version;
        config.width = 1280;
        config.height = 720;
        config.resizable = false;
        new LwjglApplication(new RaceGame(), config);
    }
}