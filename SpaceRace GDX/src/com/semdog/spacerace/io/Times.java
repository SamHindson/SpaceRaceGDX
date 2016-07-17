package com.semdog.spacerace.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;

/**
 * A class used to handle Input/Output of the races' best times. It uses a
 * serialized HashMap because they're relatively more difficult to mess with (as
 * compared to say, a JSON)
 */

public class Times implements Serializable {
	//	This is here because Eclipse tells me it needs to be here. Sure
	private static final long serialVersionUID = -6990769729054607028L;	

	private HashMap<String, Float> times;

	@SuppressWarnings("unchecked")
	public Times() {
		//	Loads up the times.
		try {
			FileInputStream fis = new FileInputStream("data/times.srt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			times = (HashMap<String, Float>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			Gdx.app.error("Times", "No times found! Writing...");
			times = new HashMap<>();
			writeTimes();
		} catch (ClassNotFoundException c) {
			Gdx.app.error("Times", "This shouldn't happen ._.");
		}

		writeTimes();
	}

	/**
	 * Serializes the times.
	 */
	public void writeTimes() {
		try {
			FileOutputStream fos = new FileOutputStream("data/times.srt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(times);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			Gdx.app.error("Times", "Failed to write times. Tell a developer!!!");
		}
	}

	public Float getTime(String id) throws NoSuchElementException {
		if (!times.containsKey(id))
			throw new NoSuchElementException();
		return times.get(id);
	}

	public void setTime(String id, float time) {
		times.put(id, time);
	}
}
