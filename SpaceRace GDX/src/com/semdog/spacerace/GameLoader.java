package com.semdog.spacerace;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * The entry point of SpaceRace.
 */

public class GameLoader {
    private static final String version = "0.3.8";

    public static void main(String[] args) {
        /* Creates a new Config file, which is used to launch the game */
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "SpaceRace v" + version;
        /* The resolution here is just for show as it is set to the proper value later on */
        config.width = 1280;
        config.height = 720;
        config.resizable = false;
        /* Makes a new LWJGL Application using the config file and the given game class */
        new LwjglApplication(new RaceGame(), config);
    }
}