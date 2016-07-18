package com.semdog.spacerace.races;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.semdog.spacerace.io.Times;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * A class which holds all the races loaded in from the races folder.
 */

public class RaceManager {
    static HashMap<String, Float> bestTimes;
    private static Array<Race> races;
    private static String currentRaceSchematics;
    private static Race currentRace;
    private static Times times;

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

    private static void loadBestTimes() {
        for (int w = 0; w < races.size; w++) {
            try {
                races.get(w).setBestTime(times.getTime(races.get(w).getID()));
            } catch (NoSuchElementException nsee) {
                System.out.println("No time found for " + races.get(w).getName());
                races.get(w).setCompleted(false);
                races.get(w).setBestTime(races.get(w).getTimeLimit());
            }
        }
    }

    public static Race getCurrentRace() {
        return currentRace;
    }

    public static void setCurrentRace(Race race) {
        currentRaceSchematics = race.getContent();
        currentRace = race;
    }

    public static Race getRace(int index) {
        return races.get(index);
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

    public static void setNewBestTime(float newBestTime) {
        currentRace.setBestTime(newBestTime);
        times.setTime(currentRace.getID(), newBestTime);
        times.writeTimes();
    }

    public static boolean[] getCompleted() {
        boolean[] completed = new boolean[races.size];

        for (int e = 0; e < completed.length; e++) {
            completed[e] = races.get(e).isCompleted();
        }

        return completed;
    }
}
