package com.semdog.spacerace.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;

import java.util.HashMap;
import java.util.Map;

/**
 * A class designed to be a place where all fonts can be used from, nullifying
 * the need to load them from the file system every time a new screen is made.
 * <p>
 * It also does away with the need for the FreeType Font library, which does not
 * work in HTML.
 *
 * @author Sam
 */

public class FontManager {
    private static HashMap<String, BitmapFont> fonts;

    public static void initialize() {
        fonts = new HashMap<>();

        load("fipps-12");
        load("fipps-16");
        load("fipps-18");
        load("fipps-20");
        load("fipps-24");
        load("fipps-32");
        load("fipps-36");
        load("fipps-36-italic");
        load("fipps-72");
        load("fipps-72-italic");
        load("inconsolata-18");
        load("inconsolata-28");
        load("inconsolata-32");
        load("inconsolata-32-italic");
        load("inconsolata-36");
        load("mohave-18");
        load("mohave-18-bold");
        load("mohave-18-italic");
        load("mohave-40");
        load("mohave-40-bold");
        load("mohave-40-italic");
        load("mohave-48");
        load("mohave-48-bold");
        load("mohave-48-italic");
        load("mohave-64");
        load("mohave-64-bold");
        load("mohave-64-italic");
        load("mohave-84");
        load("mohave-84-bold");
        load("mohave-84-italic");
    }

    /**
     * Loads a font from the file system and sticks it in the HashMap.
     *
     * @param name The name of the font
     */
    private static void load(String name) {
        FileHandle fileHandle = Gdx.files.internal("assets/fonts/" + name + ".fnt");
        BitmapFont font = new BitmapFont(fileHandle);
        fixFont(font);
        fonts.put(name, font);
    }

    /**
     * A method which resolves some font rendering issues. Credit goes to
     * StackExchange user monnef for coming up with it and mhilbrunner for
     * implementing it in Java.
     * <p>
     * (http://stackoverflow.com/questions/25011668/bitmapfont-rendering-
     * artifacts)
     *
     * @param font The font to be fixed
     */
    private static void fixFont(BitmapFont font) {
        for (Glyph[] page : font.getData().glyphs) {
            if (page == null) {
                continue;
            }

            for (Glyph glyph : page) {
                if (glyph == null) {
                    continue;
                }

                glyph.u2 -= 0.001f;
                glyph.v2 -= 0.001f;
            }
        }
    }

    /**
     * Fetches a font from the HashMap, and returns a font that is known to be
     * there if the requested one isn't.
     *
     * @param name Name of the font
     * @return The font, or a default if the requested font was not found.
     */
    public static BitmapFont getFont(String name) {
        if (fonts.containsKey(name))
            return fonts.get(name);
        else {
            Gdx.app.error("FontManager", "Font " + name + " was requested, but was not found!");
            return fonts.get("fipps-12");
        }
    }

    /**
     * Disposes of all fonts in the HashMap to free memory when app closes.
     */
    public static void dispose() {
        Gdx.app.log("FontManager", "Disposing of Font Cache");
        for (Map.Entry<String, BitmapFont> entry : fonts.entrySet()) {
            entry.getValue().dispose();
        }
        Gdx.app.log("FontManager", "Done");
    }
}
