package com.semdog.spacerace.io;

import com.badlogic.gdx.Gdx;

import java.io.*;
import java.util.HashMap;

/**
 * A class used to handle Input/Output of the races' best times.
 * It uses a serialized HashMap because they're relatively more difficult to mess with (as compared to say, a JSON)
 */

public class Times implements Serializable {
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

    public Float getTime(String id) {
        return times.getOrDefault(id, 2104f);
    }

    public void setTime(String id, float time) {
        Gdx.app.log("Times", id + " :: " + time);
        times.put(id, time);
    }
}
