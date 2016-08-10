package com.semdog.spacerace.races;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.semdog.spacerace.io.Times;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * A class which holds all the races loaded in from the races folder.
 *
 * @author Sam
 */

public class RaceManager {
    static HashMap<String, Float> bestTimes;
    private static Race[] races;
    private static Race currentRace;
    private static Times times;

    /**
     * Loads up the races
     */
    public static void initialize() {
        times = new Times();

        FileHandle[] raceFiles = Gdx.files.internal("data/stockraces").list();
        races = new Race[raceFiles.length];
        JsonReader jsonReader = new JsonReader();

        for (FileHandle fileHandle : raceFiles) {
            JsonValue data = jsonReader.parse(fileHandle);
            String name = data.getString("name");
            String description = data.getString("description");
            String briefing = data.getString("briefing");
            String author = data.getString("author");
            float timeLimit = (float) data.getDouble("timelimit");
            int index = Integer.parseInt(fileHandle.nameWithoutExtension().substring(4)) - 1;
            races[index] = new Race(name, author, description, briefing, timeLimit, fileHandle.readString());
        }

        loadBestTimes();
    }

    /**
     * Loads up the best times for each race
     */
    private static void loadBestTimes() {
        for (Race race : races) {
            try {
                race.setBestTime(times.getTime(race.getID()));
            } catch (NoSuchElementException nsee) {
                System.out.println("No time found for " + race.getName());
                race.setCompleted(false);
                race.setBestTime(race.getTimeLimit());
            }
        }
    }

    public static Race getCurrentRace() {
        return currentRace;
    }

    public static void setCurrentRace(Race race) {
        currentRace = race;
    }

    public static Race getRace(int index) {
        return races[index];
    }

    public static String[] getRaceTitles() {
        String[] titles = new String[races.length];

        for (int e = 0; e < titles.length; e++) {
            titles[e] = races[e].getName();
        }

        return titles;
    }

    public static String[] getRaceDescriptions() {
        String[] descriptions = new String[races.length];

        for (int e = 0; e < descriptions.length; e++) {
            descriptions[e] = races[e].getDescription();
        }

        return descriptions;
    }

    public static String[] getRaceTimeLimits() {
        String[] limits = new String[races.length];

        for (int e = 0; e < limits.length; e++) {
            limits[e] = races[e].getTimeLimit() + "s";
        }

        return limits;
    }

    /**
     * If a record has been broken, let the Times manager know
     */
    public static void setNewBestTime(float newBestTime) {
        currentRace.setBestTime(newBestTime);
        times.setTime(currentRace.getID(), newBestTime);
        times.writeTimes();
    }

    public static boolean[] getCompleted() {
        boolean[] completed = new boolean[races.length];

        for (int e = 0; e < completed.length; e++) {
            completed[e] = races[e].isCompleted();
        }

        return completed;
    }
}
