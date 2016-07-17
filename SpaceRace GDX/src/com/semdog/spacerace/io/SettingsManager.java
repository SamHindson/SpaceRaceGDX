package com.semdog.spacerace.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.semdog.spacerace.audio.SoundManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam on 2016/07/05.
 * <p>
 * The class that handles the input/output of the game's settings
 */

public class SettingsManager {
    // The default key bindings.
    private static final int DEF_LEFT = Input.Keys.A;
    private static final int DEF_RIGHT = Input.Keys.D;
    private static final int DEF_JUMP = Input.Keys.SPACE;
    private static final int DEF_GRENADE = Input.Keys.G;
    private static final int DEF_ACTIVATE = Input.Keys.E;
    private static final int DEF_ENGINES = Input.Keys.W;
    private static final int DEF_PAUSE = Input.Keys.ESCAPE;
    private static final int DEF_GOGGLES = Input.Keys.Q;
    private static final int DEF_LOCK = Input.Keys.C;
    private static final int DEF_SPRINT = Input.Keys.SHIFT_LEFT;
    private static final int DEF_RCS_UP = Input.Keys.UP;
    private static final int DEF_RCS_DOWN = Input.Keys.DOWN;
    private static final int DEF_RCS_LEFT = Input.Keys.LEFT;
    private static final int DEF_RCS_RIGHT = Input.Keys.RIGHT;

    private static float master = 100;
    private static float sfx = 100;
    private static float music = 100;
    private static boolean fullscreen = false;
    private static String resolution = "1280x720";
    private static int width, height;
    private static Preferences preferences;
    private static Preferences keyBindings;

    private static HashMap<String, Integer> keys;

    private static boolean firstTime;

    private static String mpName;

    public static void initialize() {
        preferences = Gdx.app.getPreferences("srsettings");
        keyBindings = Gdx.app.getPreferences("srskeys");

        // If our preferences does not contain something as elementary as the
        // fullscreen option, that means it probably does not exist yet. So we
        // create it.
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

        firstTime = !preferences.contains("firsttime") || preferences.getBoolean("firsttime");

        keys = new HashMap<>();

        // Likewise, if the key bindings doesn't know what LEFT means, it was
        // probably instantiated mere seconds ago and has none of that
        // information on hand.
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
            keys.put("LOCK", DEF_LOCK);
            keys.put("SPRINT", DEF_SPRINT);
            keys.put("RCS_UP", DEF_RCS_UP);
            keys.put("RCS_DOWN", DEF_RCS_DOWN);
            keys.put("RCS_LEFT", DEF_RCS_LEFT);
            keys.put("RCS_RIGHT", DEF_RCS_RIGHT);
            writeKeys();
        } else {
            //	If it does, it simply sticks the loaded keys into our key map.
            for (Map.Entry<String, ?> entry : keyBindings.get().entrySet()) {
                keys.put(entry.getKey(), Integer.parseInt("" + entry.getValue()));
            }
        }

        //	Sets the volumes loaded from the preferences.
        SoundManager.setMasterVolume(preferences.getFloat("master"));
        SoundManager.setMusicVolume(preferences.getFloat("music"));
        SoundManager.setSfxVolume(preferences.getFloat("sfx"));
    }

    public static HashMap<String, Integer> getKeys() {
        return keys;
    }

    public static void writeKeys() {
        for (Map.Entry<String, Integer> entry : keys.entrySet()) {
            keyBindings.putInteger(entry.getKey(), entry.getValue());
        }
        keyBindings.flush();
    }

    public static void writeSettings() {
        preferences.putBoolean("firsttime", firstTime);
        preferences.putBoolean("fullscreen", fullscreen);
        preferences.putString("resolution", resolution);
        preferences.putFloat("master", master);
        preferences.putFloat("sfx", sfx);
        preferences.putFloat("music", music);
        preferences.flush();
    }

    public static float getMaster() {
        return master;
    }

    public static void setMaster(float master) {
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

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setKeys(String[] titles, int[] keyCodes) {
        System.out.println("Wew");
        for (int a = 0; a < titles.length; a++) {
            System.out.println(titles[a] + " = " + keyCodes[a]);
            String[] parts = titles[a].toUpperCase().split(" ");
            keys.put(parts[parts.length - 1], keyCodes[a]);
        }
    }

    public static int getKey(String keyName) {
        if (keys.containsKey(keyName)) {
            return keys.get(keyName);
        } else {
            Gdx.app.error("SettingsManager", "NO SUCH KEY! " + keyName);
            return -15;
        }
    }

    public static boolean isFirstTime() {
        return firstTime;
    }

    public static void setFirstTime(boolean firstTime) {
        SettingsManager.firstTime = firstTime;
    }

    public static String getName() {
        return mpName;
    }

    public static void setName(String mpName) {
        SettingsManager.mpName = mpName;
    }
}
