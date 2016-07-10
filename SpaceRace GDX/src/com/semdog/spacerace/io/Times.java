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
 * A class used to handle Input/Output of the races' best times.
 * It uses a serialized HashMap because they're relatively more difficult to mess with (as compared to say, a JSON)
 */

public class Times implements Serializable {
	private static final long serialVersionUID = -6990769729054607028L;
	
	private HashMap<String, Float> times;

    public Times() {
        try {
            FileInputStream fis = new FileInputStream("data/times.srt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            times = (HashMap) ois.readObject();
            ois.close();
            fis.close();
            Gdx.app.log("Times", "Managed to read my times well.");
        } catch (IOException ioe) {
            Gdx.app.error("Times", "No times found! Writing...");
            times = new HashMap<>();
            writeTimes();
        } catch (ClassNotFoundException c) {
            Gdx.app.error("Times", "This shouldn't happen ._.");
        }

        writeTimes();
    }

    public void writeTimes() {
        try {
            FileOutputStream fos =
                    new FileOutputStream("data/times.srt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(times);
            oos.close();
            fos.close();
            Gdx.app.log("Times", "Done writing.");
        } catch (IOException ioe) {
            Gdx.app.error("Times", "Bad bad bad!");
        }
    }

    public Float getTime(String id) throws NoSuchElementException {
        if (!times.containsKey(id))
            throw new NoSuchElementException();
        return times.get(id);
    }

    public void setTime(String id, float time) {
        Gdx.app.log("Times", id + " :: " + time);
        times.put(id, time);
    }
}
