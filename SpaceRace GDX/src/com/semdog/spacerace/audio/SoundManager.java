package com.semdog.spacerace.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundManager {

	static HashMap<String, Sound> clips;
	static HashMap<String, Long> looping;

    static HashMap<String, Music> music;

	public static void initialize() {
		clips = new HashMap<>();
        looping = new HashMap<>();

        music = new HashMap<>();

		load("explosion1.ogg");
		load("explosion2.ogg");
		load("explosion3.ogg");
		load("beep.wav");
		load("jump.ogg");
		load("egress.ogg");
		load("ingress.ogg");
		load("bulletground.wav");
		load("shrap1.ogg");
		load("shrap2.ogg");
		load("shrap3.ogg");
		load("runt.wav");
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
		load("goggleson.wav");
		load("gogglesoff.wav");

		load("neet.wav");

        load("victory.ogg");
        load("failure.ogg");

        loadMusic("oxidiser");
        loadMusic("menu");
    }

    static void loadMusic(String name) {
        music.put(name, Gdx.audio.newMusic(Gdx.files.internal("assets/music/" + name + ".ogg")));
    }

    public static void playMusic(String name) {
        if (music.containsKey(name)) {
            music.get(name).setLooping(true);
            music.get(name).play();
            Gdx.app.log("SoundManager", "Playing song " + name);
        } else {
            Gdx.app.error("SoundManager", "No such song!");
        }
    }

    public static void stopMusic(String name) {
        if (music.containsKey(name)) {
            music.get(name).stop();
            Gdx.app.log("SoundManager", "Stopped song.");
        } else {
            Gdx.app.error("SoundManager", "No such song!");
        }
    }

	public static void load(String name) {
		System.out.println(name);
		clips.put(name.split("[.]")[0], Gdx.audio.newSound(Gdx.files.internal("assets/audio/" + name)));
	}

	public static void playSound(String name, float volume, float pan) {
		if (clips.containsKey(name)) {
			clips.get(name).play(volume, 1, pan);
		} else {
			Gdx.app.error("SoundManager", "No spund: " + name);
		}
	}

	public static void loopSound(String name, float volume, float pan) {
		if (!looping.containsKey(name)) {
			if (clips.containsKey(name)) {
				looping.put(name, clips.get(name).loop(volume, 1, pan));
			} else {
				Gdx.app.error("SoundManager", "No! " + name);
			}
		}
	}

	public static void stopSound(String name) {
		if (looping.containsKey(name)) {
			clips.get(name).stop(looping.get(name));
			looping.remove(name);
		}
	}
}
