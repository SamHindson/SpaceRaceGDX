package com.semdog.spacerace.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

/**
 * A class which handles all of the graphical input of SpaceRace, inspired by
 * Notch's architecture in games such as Prelude of the Chambered and Metagun.
 */

public class Art {
    /* HashMaps which store the textures and predominant colors of each piece of art found in SpaceRace. */
    private static HashMap<String, Texture> artwork;
    private static HashMap<String, Color> accents;

    public static void initialize() {
        artwork = new HashMap<>();
        accents = new HashMap<>();

        // Create an error texture
        Pixmap errorPix = new Pixmap(50, 50, Format.RGBA8888);
        errorPix.setColor(Color.PINK);
        errorPix.fill();
        errorPix.setColor(Color.BLACK);
        errorPix.fillTriangle(0, 0, 50, 0, 25, 50);

        artwork.put("error", new Texture(errorPix));
        artwork.put("pixel_white", createPixel(Color.WHITE));

        FileHandle[] graphics = Gdx.files.internal("assets/graphics").list();
        for (FileHandle graphic : graphics) {
            if (!graphic.extension().equals("png"))
                continue;
            load(graphic);
        }
    }

    /**
     * Creates a texture of a certain color that is 1x1 in size. Practically
     * only ever used for the white pixel.
     */
    private static Texture createPixel(Color color) {
        Pixmap pix = new Pixmap(1, 1, Format.RGB565);
        pix.setColor(color);
        pix.drawPixel(0, 0);
        return new Texture(pix);
    }

    /**
     * Calculates the saturation value of a color using R, G and B values.
     * Credit goes to StackOverflow user Mohsen
     * (http://stackoverflow.com/questions/2353211/hsl-to-rgb-color-conversion)
     */
    private static double getSaturation(float r, float g, float b) {
        double max = Math.max(r, g);
        max = Math.max(max, b);
        double min = Math.min(r, g);
        min = Math.min(min, b);
        double sat, lum = (max + min) / 2;

        if (max == min) {
            sat = 0;
        } else {
            double d = max - min;
            sat = lum > 0.5 ? d / (2 - max - min) : d / (max + min);
        }

        return sat;
    }

    /**
     * Creates a silhouette of a given pixmap. Used mainly when a mass has
     * damage done to it and it needs that classic flashing look.
     */
    private static Texture createSilhouette(Pixmap oldPixmap) {
        Pixmap newPixmap = new Pixmap(oldPixmap.getWidth(), oldPixmap.getHeight(), Format.RGBA8888);
        newPixmap.setColor(Color.WHITE);

        for (int x = 0; x < newPixmap.getWidth(); x++) {
            for (int y = 0; y < newPixmap.getHeight(); y++) {
                int color = oldPixmap.getPixel(x, y);
                int alpha = color & 0x000000FF;
                newPixmap.setColor(new Color(1, 1, 1, alpha / 255.f));
                newPixmap.drawPixel(x, y);
            }
        }

        return new Texture(newPixmap);
    }

    /**
     * Loads a texture from a FileHandle, and puts that, its silhouette and its
     * predominant color in the respective hashmaps.
     */
    private static void load(FileHandle file) {
        Texture texture = new Texture(file);
        String name = file.nameWithoutExtension();
        artwork.put(name, texture);

        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }

        Pixmap pixmap = texture.getTextureData().consumePixmap();
        artwork.put(name + "_sil", createSilhouette(pixmap));

        double maxSaturation = 0;

        // The coordinates of the pixel with the highest saturation.
        int mx = 0, my = 0;

        // Scans through entire picture looking for pixel with highest saturation.
        for (int x = 0; x < texture.getWidth(); x++) {
            for (int y = 0; y < texture.getWidth(); y++) {
                int raw = pixmap.getPixel(x, y);
                Color color = new Color(raw);

                double saturation = getSaturation(color.r, color.g, color.b);

                if (saturation > maxSaturation) {
                    mx = x;
                    my = y;
                    maxSaturation = saturation;
                }
            }
        }

        Color highlight = new Color(pixmap.getPixel(mx, my));
        pixmap.dispose();

        accents.put(name, highlight);
    }

    /**
     * Returns a texture from the textures HashMap by using the texture's ID.
     */
    public static Texture get(String name) {
        if (artwork.containsKey(name)) {
            return artwork.get(name);
        } else {
            try {
                Texture texture = new Texture(Gdx.files.internal("assets/graphics/" + name + ".png"));
                Gdx.app.error("Art", "WARNING! " + name + " was not loaded initially and as such caused a slowdown. Tell a developer!");
                artwork.put(name, texture);
                return texture;
            } catch (Exception e) {
                System.err.println("Problem loading texture " + name + "! Here's an error one instead.");
                return artwork.get("error");
            }
        }
    }

    /**
     * Returns a texture's accent color from the accents HashMap by using the
     * texture's ID.
     */
    public static Color getAccent(String textureName) {
        return accents.get(textureName);
    }

    /**
     * Clears everything.
     */
    public static void dispose() {
        for (Map.Entry<String, Texture> entry : artwork.entrySet()) {
            entry.getValue().dispose();
        }
    }
}
