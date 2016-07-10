package com.semdog.spacerace.io;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

/**
 * Created by Sam on 2016/07/05.
 * <p>
 * The class that handles the input/output of the game's settings
 */

public class SettingsManager {
    public static final int DEF_LEFT = Input.Keys.A;
    public static final int DEF_RIGHT = Input.Keys.D;
    public static final int DEF_JUMP = Input.Keys.SPACE;
    public static final int DEF_GRENADE = Input.Keys.G;
    public static final int DEF_ACTIVATE = Input.Keys.E;
    public static final int DEF_ENGINES = Input.Keys.W;
    public static final int DEF_PAUSE = Input.Keys.ESCAPE;
    public static final int DEF_GOGGLES = Input.Keys.Q;
    public static final int DEF_SPRINT = Input.Keys.SHIFT_LEFT;
    private static float master = 100;
    private static float sfx = 100;
    private static float music = 100;
    private static boolean fullscreen = false;
    private static String resolution = "1280x720";
    private static int width, height;
    private static Preferences preferences;
    private static Preferences keyBindings;
    private static HashMap<String, Integer> keys;

    public static void initialize() {
        preferences = Gdx.app.getPreferences("srsettings");
        keyBindings = Gdx.app.getPreferences("srskeys");
        if (!preferences.contains("fullscreen")) {
            Gdx.app.error("SettingsManager", "No preferences found. Writing now...");
            setFullscreen(false);
            setResolution("1280x720");
            setMaster(100);
            setSfx(100);
            setMusic(100);
            writeSettings();
        } else {
            fullscreen = preferences.getBoolean("fullscreen");
            master = preferences.getFloat("master");
            sfx = preferences.getFloat("sfx");
            music = preferences.getFloat("music");
            resolution = preferences.getString("resolution");

            width = Integer.parseInt(resolution.split("x")[0]);
            height = Integer.parseInt(resolution.split("x")[1]);
        }

        keys = new HashMap<>();

        if (!keyBindings.contains("LEFT")) {
            Gdx.app.error("SettingsManager", "No Controls, Man!");
            keys.put("LEFT", DEF_LEFT);
            keys.put("RIGHT", DEF_RIGHT);
            keys.put("JUMP", DEF_JUMP);
            keys.put("GRENADE", DEF_GRENADE);
            keys.put("ACTIVATE", DEF_ACTIVATE);
            keys.put("ENGINES", DEF_ENGINES);
            keys.put("PAUSE", DEF_PAUSE);
            keys.put("GOGGLES", DEF_GOGGLES);
            keys.put("SPRINT", DEF_SPRINT);
            writeKeys();
        } else {
            for (Map.Entry<String, ?> entry : keyBindings.get().entrySet()) {
                keys.put(entry.getKey(), Integer.parseInt("" + entry.getValue()));
            }
        }
    }

    public static HashMap<String, Integer> getKeys() {
        return keys;
    }

    private static void writeKeys() {
        for (Map.Entry<String, Integer> entry : keys.entrySet()) {
            keyBindings.putInteger(entry.getKey(), entry.getValue());
        }
        keyBindings.flush();
    }

    public static void writeSettings() {
        preferences.putBoolean("fullscreen", fullscreen);
        preferences.putString("resolution", resolution);
        preferences.putFloat("master", master);
        preferences.putFloat("sfx", sfx);
        preferences.putFloat("music", music);
        Gdx.app.log("SettingsManager", "Master: " + master);
        preferences.flush();
    }

    public static float getMaster() {
        return master;
    }

    public static void setMaster(float master) {
        Gdx.app.log("SettingsManager", "Setting master to " + master);
        SettingsManager.master = master;
    }

    public static float getMusic() {
        return music;
    }

    public static void setMusic(float music) {
        SettingsManager.music = music;
    }

    public static float getSfx() {
        return sfx;
    }

    public static void setSfx(float sfx) {
        SettingsManager.sfx = sfx;
    }

    public static String getResolution() {
        return resolution;
    }

    public static void setResolution(String resolution) {
        SettingsManager.resolution = resolution;
        width = Integer.parseInt(resolution.split("x")[0]);
        height = Integer.parseInt(resolution.split("x")[1]);
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static void setFullscreen(boolean fullscreen) {
        SettingsManager.fullscreen = fullscreen;
    }

    public static void setKeys(String[] titles, int[] keyCodes) {
        for (int a = 0; a < titles.length; a++) {
            keys.put(titles[a].toUpperCase(), keyCodes[a]);
        }
        writeKeys();
    }

    public static int getKey(String keyName) {
        if (keys.containsKey(keyName)) {
            return keys.get(keyName);
        } else {
            Gdx.app.error("SettingsManager", "NO SUCH KEY! " + keyName);
            return -15;
        }
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }
}
