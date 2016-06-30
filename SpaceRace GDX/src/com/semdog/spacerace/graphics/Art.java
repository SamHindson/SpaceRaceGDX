package com.semdog.spacerace.graphics;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

public class Art {
	private static HashMap<String, Texture> artwork;

	public static void initialize() {
		artwork = new HashMap<>();

		Pixmap errorPix = new Pixmap(50, 50, Format.RGBA8888);
		errorPix.setColor(Color.PINK);
		errorPix.fill();
		errorPix.setColor(Color.BLACK);
		errorPix.fillTriangle(0, 0, 50, 0, 25, 50);

		artwork.put("error", new Texture(errorPix));

		artwork.put("pixel_red", createPixel(Color.RED));
		artwork.put("pixel_lightred", createPixel(new Color(1f, 0.5f, 0.5f, 1.f)));
		artwork.put("pixel_orange", createPixel(Color.ORANGE));
		artwork.put("pixel_yellow", createPixel(Color.YELLOW));
		artwork.put("pixel_green", createPixel(Color.GREEN));
		artwork.put("pixel_blue", createPixel(Color.BLUE));
		artwork.put("pixel_purple", createPixel(Color.PURPLE));
		artwork.put("pixel_gray", createPixel(Color.GRAY));
		artwork.put("pixel_white", createPixel(Color.WHITE));
		artwork.put("pixel_pink", createPixel(new Color(1, 0, 110.f / 225.f, 1.f)));

		Gdx.app.log("Art", "Loading artworks...");
		FileHandle[] graphics = Gdx.files.internal("assets/graphics").list();
		for (FileHandle graphic : graphics) {
			if (!graphic.extension().equals("png"))
				continue;
			load(graphic);
		}
		Gdx.app.log("Art", "Done.");
	}

	private static Texture createPixel(Color color) {
		Pixmap pix = new Pixmap(1, 1, Format.RGB565);
		pix.setColor(color);
		pix.drawPixel(0, 0);
		System.out.println("Created pixel for " + color);
		return new Texture(pix);
	}

	private static Texture createSilhouette(Texture original) {
		if (!original.getTextureData().isPrepared()) {
			System.out.println("Not premared yet");
			original.getTextureData().prepare();
		}

		Pixmap oldPixmap = original.getTextureData().consumePixmap();
		Pixmap newPixmap = new Pixmap(original.getWidth(), original.getHeight(), Format.RGBA8888);
		newPixmap.setColor(Color.WHITE);

		for (int x = 0; x < newPixmap.getWidth(); x++) {
			for (int y = 0; y < newPixmap.getHeight(); y++) {
				int color = oldPixmap.getPixel(x, y);
				int alpha = color & 0x000000FF;
				newPixmap.setColor(new Color(1, 1, 1, alpha/255.f));
				newPixmap.drawPixel(x, y);
			}
		}

		Texture texture = new Texture(newPixmap);
		return texture;
	}

	public static void load(FileHandle file) {
		Texture texture = new Texture(file);
		String name = file.nameWithoutExtension();
		artwork.put(name, texture);
		artwork.put(name + "_sil", createSilhouette(texture));
		Gdx.app.log("Art", "Loaded " + file);
	}

	public static Texture get(String name) {
		if (artwork.containsKey(name)) {
			return artwork.get(name);
		} else {
			try {
				Texture texture = new Texture(Gdx.files.internal("assets/graphics/" + name + ".png"));
				Gdx.app.error("Art",
						"WARNING! " + name + " was not loaded intitially and as such caused a slowdown. Nyet!");
				artwork.put(name, texture);
				return texture;
			} catch (Exception e) {
				System.err.println("Error loading your texture! Here's an error one instead.");
				return artwork.get("error");
			}
		}
	}
}
