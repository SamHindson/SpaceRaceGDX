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
 */

public class RaceManager {
    static HashMap<String, Float> bestTimes;
    private static Race[] races;
    private static Race currentRace;
    private static Times times;

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

    private static void loadBestTimes() {
        for (int w = 0; w < races.length; w++) {
            try {
                races[w].setBestTime(times.getTime(races[w].getID()));
            } catch (NoSuchElementException nsee) {
                System.out.println("No time found for " + races[w].getName());
                races[w].setCompleted(false);
                races[w].setBestTime(races[w].getTimeLimit());
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
