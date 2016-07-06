package com.semdog.spacerace.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.semdog.spacerace.io.SettingsManager;

import java.util.HashMap;
import java.util.Map;

// TODO fix the music queue

public class SoundManager {

    private static HashMap<String, Sound> clips;
    private static HashMap<String, Long> looping;

    private static HashMap<String, Music> music;
    private static Array<String> queuedMusic;

	public static void initialize() {
		clips = new HashMap<>();
        looping = new HashMap<>();

        music = new HashMap<>();

        queuedMusic = new Array<>();

		load("explosion1.ogg");
		load("explosion2.ogg");
		load("explosion3.ogg");
		load("beep.wav");
		load("jump.ogg");
        load("egress.wav");
        load("ingress.wav");
        load("bulletground.wav");
		load("shrap1.ogg");
		load("shrap2.ogg");
		load("shrap3.ogg");
		load("runt.wav");
        load("needle.wav");
        load("carbine.wav");
		load("smg.wav");
		load("shotgun.wav");
		load("rocketlaunch.wav");

		load("runtgun.wav");
		load("playerhit1.wav");
		load("playerhit2.wav");
		load("playerhit3.wav");
		load("playerhit4.wav");
		load("playerhit5.wav");
		load("shiphurt.wav");

		load("healthget.wav");
        load("toastget.wav");
        load("weaponget.wav");

        load("goggleson.wav");
        load("gogglesoff.wav");

		load("neet.wav");

        loadMusic("victory.ogg");
        loadMusic("failure.ogg");
        loadMusic("failure2.ogg");
        loadMusic("oxidiser.ogg");
        loadMusic("menu.ogg");
    }

    private static void loadMusic(String name) {
        music.put(name.split("[.]")[0], Gdx.audio.newMusic(Gdx.files.internal("assets/music/" + name)));
    }

    public static boolean isMusicPlaying(String name) {
        return music.get(name).isPlaying();
    }

    public static void playMusic(String name, boolean loop) {
        if (music.containsKey(name)) {
            try {
                music.get(name).stop();
                music.get(name).setLooping(loop);
                music.get(name).setVolume(SettingsManager.getMaster() / 100.f * SettingsManager.getMusic() / 100.f);
                music.get(name).play();
                Gdx.app.log("SoundManager", "Playing song " + name);
            } catch (GdxRuntimeException e) {
                if (queuedMusic.contains(name + "-" + loop, true))
                    return;
                Gdx.app.error("SoundManager", "There was an error processing that. We'll sort it out next frame");
                queuedMusic.add(name + "-" + loop);
            }
        } else {
            Gdx.app.error("SoundManager", name + " is not part of our music bank.");
        }
    }

    public static void stopMusic(String name) {
        if (music.containsKey(name)) {
            if (music.get(name).isPlaying()) {
                Gdx.app.log("SoundManager", "Stopping music " + name);
                music.get(name).stop();
                music.get(name).setLooping(false);
                music.get(name).dispose();
            } else {
                Gdx.app.error("SoundManager", name + " wasn't playing.");
            }
        } else {
            Gdx.app.error("SoundManager", name + " hasn't been composed yet.");
        }
    }

    private static void load(String name) {
        clips.put(name.split("[.]")[0], Gdx.audio.newSound(Gdx.files.internal("assets/audio/" + name)));
	}

    public static void stopAllSounds() {
        for (Map.Entry<String, Sound> entry : clips.entrySet()) {
            entry.getValue().stop();
        }
    }

	public static void playSound(String name, float volume, float pan) {
		if (clips.containsKey(name)) {
            clips.get(name).play(volume * SettingsManager.getMaster() / 100.f * SettingsManager.getSfx() / 100.f, 1, pan);
        } else {
            Gdx.app.error("SoundManager", name + " is not part of our sound bank.");
        }
	}

	public static void loopSound(String name, float volume, float pan) {
		if (!looping.containsKey(name)) {
			if (clips.containsKey(name)) {
                looping.put(name, clips.get(name).loop(volume * SettingsManager.getMaster() / 100.f * SettingsManager.getSfx() / 100.f, 1, pan));
            } else {
                Gdx.app.error("SoundManager", name + " is not part of our sound bank.");
            }
		}
	}

	public static void stopSound(String name) {
		if (looping.containsKey(name)) {
			clips.get(name).stop(looping.get(name));
			looping.remove(name);
		}
	}

    public static void update() {
        if (queuedMusic.size > 0) {
            String title = queuedMusic.first();
            Gdx.app.log("SoundManager", title + " requested...");
            playMusic(title.split("-")[0], Boolean.parseBoolean(title.split("-")[1]));
            queuedMusic.removeValue(title, true);
        }
    }
}
