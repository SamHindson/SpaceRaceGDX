package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.semdog.spacerace.misc.Tools;

/**
 * A class which handles the various snarky messages one sees on the HUD when the die.
 */

public class LifeAndDeath {
    private static String[] condolences;
    private static String[] failures;

    static {
        FileHandle condolencesFile = Gdx.files.internal("assets/text/condolences.sr");
        String full = condolencesFile.readString();
        condolences = full.split("\n");

        FileHandle failuresFile = Gdx.files.internal("assets/text/failures.sr");
        full = failuresFile.readString();
        failures = full.split("\n");
    }

    public static String getRandomCondolence() {
        return (String) Tools.decide((Object[]) condolences);
    }

    public static String getRandomFailure() {
        return (String) Tools.decide((Object[]) failures);
    }
}
