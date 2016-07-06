package com.semdog.spacerace;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GameLoader {
    public static final String version = "0.3.0";

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "SpaceRace v" + version;
        config.width = 1280;
        config.height = 720;
        config.samples = 4;
        config.resizable = false;
        new LwjglApplication(new RaceGame(), config);
    }
}