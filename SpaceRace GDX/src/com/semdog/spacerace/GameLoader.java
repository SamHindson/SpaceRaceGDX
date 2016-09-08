package com.semdog.spacerace;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.semdog.spacerace.net.SpaceRaceServer;

/**
 * The entry point of SpaceRace. Can be used to launch either the real game or a dedicated server,
 * depending on which arguments it is passed. (Pass "-server" to start a server, or leave it
 * blank for a Singleplayer adventure)
 */

public class GameLoader {
    private static final String version = "0.3.8";

    public static void main(String[] args) {
        if (args.length == 0) {
            /* Creates a new Config file, which is used to launch the game */
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.title = "SpaceRace v" + version;
			/* The resolution here is just for show as it is set to the proper value later on */
            config.width = 1280;
            config.height = 720;
            config.resizable = false;
			/* Makes a new LWJGL Application using the config file and the given game class */
            new LwjglApplication(new RaceGame(), config);
        } else if (args[0].equals("-server")) {
            System.out.println("Got it! Starting a server...");
            try {
                new SpaceRaceServer();
            } catch (Exception e) {
                System.err.println("Ouch! Couldn't start the server.");
                e.printStackTrace();
            }
        } else {
            System.err.println("I guarantee you, some of those arguments don't belong here.");
        }
    }
}