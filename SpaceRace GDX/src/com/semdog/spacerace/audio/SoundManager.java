package com.semdog.spacerace.audio;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * A class which handles the audio of SpaceRace.
 * 
 * @author Sam
 *
 */

public class SoundManager {
	private static float masterVolume;
	private static float musicVolume;
	private static float sfxVolume;

	private static HashMap<String, Sound> clips;
	private static HashMap<String, Long> looping;

	private static HashMap<String, Music> music;
	private static Array<String> queuedMusic;

	/**
	 * Loads the required sounds into the RAM all at once. TODO: if this game
	 * gets any bigger, find a better way to do this.
	 */
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
		load("rubbish.wav");
		load("runt.wav");
		load("needle.wav");
		load("carbine.wav");
		load("smg.wav");
		load("shotgun.wav");
		load("rocketlaunch.wav");
		load("grenadebeep.wav");

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
		load("ammoget.wav");
		load("fuelget.wav");
		load("fueldrink.wav");

		load("goggleson.wav");
		load("gogglesoff.wav");

		load("neet.wav");
		load("rcs.wav");

		loadMusic("victory.ogg");
		loadMusic("failure.ogg");
		loadMusic("failure2.ogg");
		loadMusic("oxidiser.ogg");
		loadMusic("menu.ogg");
	}

	/**
	 * Loads music from a file name.
	 * 
	 * @param name
	 */
	private static void loadMusic(String name) {
		music.put(name.split("[.]")[0], Gdx.audio.newMusic(Gdx.files.internal("assets/music/" + name)));
	}

	/**
	 * Loads a sound bite from a file name.
	 * 
	 * @param name
	 */
	private static void load(String name) {
		clips.put(name.split("[.]")[0], Gdx.audio.newSound(Gdx.files.internal("assets/audio/" + name)));
	}

	/**
	 * Plays a sound at a given volume and panning.
	 * 
	 * @param name
	 * @param volume
	 * @param pan
	 */
	public static void playSound(String name, float volume, float pan) {
		if (clips.containsKey(name)) {
			clips.get(name).play(volume * masterVolume * sfxVolume, 1, pan);
		} else {
			Gdx.app.error("SoundManager", name + " is not part of our sound bank.");
		}
	}

	/**
	 * Loops a sound at a given volume and panning.
	 * 
	 * @param name
	 * @param volume
	 * @param pan
	 */
	public static void loopSound(String name, float volume, float pan) {
		if (!looping.containsKey(name)) {
			if (clips.containsKey(name)) {
				looping.put(name, clips.get(name).loop(volume * masterVolume * sfxVolume, 1, pan));
			} else {
				Gdx.app.error("SoundManager", name + " is not part of our sound bank.");
			}
		}
	}

	/**
	 * Plays a piece of music.
	 * 
	 * @param name
	 * @param loop
	 */
	public static void playMusic(String name, boolean loop) {
		if (music.containsKey(name)) {
			try {
				if(music.get(name).isPlaying())
					return;
				music.get(name).stop();
				music.get(name).setLooping(loop);
				music.get(name).setVolume(masterVolume * musicVolume);
				music.get(name).play();
				
				// If the manager fails to play a song, it can be solved by
				// requesting it be played again next frame. So we add it to the
				// queue of songs waiting to be born.
			} catch (GdxRuntimeException e) {
				if (queuedMusic.contains(name + "-" + loop, true))
					return;
				queuedMusic.add(name + "-" + loop);
			}
		} else {
			Gdx.app.error("SoundManager", name + " is not part of our music bank.");
		}
	}

	/**
	 * Checks whether a song is playing.
	 * @param name
	 * @return
	 */
	public static boolean isMusicPlaying(String name) {
		return music.get(name).isPlaying();
	}

	/**
	 * Stops a particular sound that was busy looping.
	 * @param name
	 */
	public static void stopSound(String name) {
		if (looping.containsKey(name)) {
			clips.get(name).stop(looping.get(name));
			looping.remove(name);
		}
	}

	/**
	 * Cuts the noise.
	 */
	public static void stopAllSounds() {
		for (Map.Entry<String, Sound> entry : clips.entrySet()) {
			entry.getValue().stop();
			entry.getValue().dispose();
		}
	}

	/**
	 * Kills the beat.
	 */
	public static void stopAllMusic() {
		for (Map.Entry<String, Music> entry : music.entrySet()) {
			entry.getValue().stop();
			entry.getValue().dispose();
		}
	}

	/**
	 * Stops a piece of music.
	 * @param name
	 */
	public static void stopMusic(String name) {
		if (music.containsKey(name)) {
			if (music.get(name).isPlaying()) {
				music.get(name).stop();
				music.get(name).setLooping(false);
				music.get(name).dispose();
			} else {
				Gdx.app.error("SoundManager", name + " wasn't playing in the first place.");
			}
		} else {
			Gdx.app.error("SoundManager", name + " hasn't been composed yet.");
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

	/**
	 * Sets the overall volume.
	 * @param volume
	 */
	public static void setMasterVolume(float volume) {
		masterVolume = volume / 10.f;
		System.out.println("Master is now " + volume);
		setMusicVolume(musicVolume);
		setSfxVolume(sfxVolume);
	}

	/**
	 * Sets the overall music volume.
	 * @param volume
	 */
	public static void setMusicVolume(float volume) {
		musicVolume = volume / 10.f;
		for (Map.Entry<String, Music> entry : music.entrySet()) {
			if (entry.getValue().isPlaying()) {
				entry.getValue().setVolume(masterVolume * musicVolume);
				System.out.println(masterVolume * musicVolume);
			}
		}
	}

	/**
	 * Sets the overall sfx volume.
	 * @param volume
	 */
	public static void setSfxVolume(float volume) {
		sfxVolume = volume / 10.f;
	}

	/**
	 * Clears all sounds from the RAM.
	 */
	public static void dispose() {
		for (Map.Entry<String, Sound> entry : clips.entrySet()) {
			entry.getValue().dispose();
		}

		for (Map.Entry<String, Music> entry : music.entrySet()) {
			entry.getValue().dispose();
		}
	}
}
