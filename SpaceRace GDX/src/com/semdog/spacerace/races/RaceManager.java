package com.semdog.spacerace.races;

import java.util.HashMap;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.semdog.spacerace.io.Times;

/**
 * Created by Sam on 2016/07/04.
 */
public class RaceManager {
    static Array<Race> races;

    static HashMap<String, Float> bestTimes;

    static String currentRaceSchematics;
    static Race currentRace;
    static Times times;

    public static void initialize() {
        races = new Array<>();

        times = new Times();

        FileHandle[] raceFiles = Gdx.files.internal("data/stockraces").list();

        JsonReader jsonReader = new JsonReader();

        for (FileHandle fileHandle : raceFiles) {
            JsonValue data = jsonReader.parse(fileHandle);
            String name = data.getString("name");
            String description = data.getString("description");
            String briefing = data.getString("briefing");
            String author = data.getString("author");

            float timeLimit = (float) data.getDouble("timelimit");

            races.add(new Race(name, author, description, briefing, timeLimit, fileHandle.readString()));
        }

        loadBestTimes();
    }

    public static String[] getRaceTitles() {
        String[] titles = new String[races.size];

        for (int e = 0; e < titles.length; e++) {
            titles[e] = races.get(e).getName();
        }

        return titles;
    }

    public static String[] getRaceDescriptions() {
        String[] descriptions = new String[races.size];

        for (int e = 0; e < descriptions.length; e++) {
            descriptions[e] = races.get(e).getDescription();
        }

        return descriptions;
    }

    public static String[] getRaceTimeLimits() {
        String[] limits = new String[races.size];

        for (int e = 0; e < limits.length; e++) {
            limits[e] = races.get(e).getTimeLimit() + "s";
        }

        return limits;
    }

    public static void loadBestTimes() {
        for (int w = 0; w < races.size; w++) {
            try {
                races.get(w).setBestTime(times.getTime(races.get(w).getID()));
            } catch (NoSuchElementException nsee) {
                races.get(w).setBestTime(races.get(w).getTimeLimit());
            }
        }
    }

    public static Race getRace(int index) {
        return races.get(index);
    }

    public static String getCurrentRaceSchematics() {
        return currentRaceSchematics;
    }

    public static float getCurrentBestTime() {
        return currentRace.getBestTime();
    }

    public static void setNewBestTime(float newBestTime) {
        currentRace.setBestTime(newBestTime);
        times.setTime(currentRace.getID(), newBestTime);
        times.writeTimes();
    }

    public static Race getCurrentRace() {
        return currentRace;
    }

    public static void setCurrentRace(Race race) {
        currentRaceSchematics = race.getContent();
        currentRace = race;
    }
}
