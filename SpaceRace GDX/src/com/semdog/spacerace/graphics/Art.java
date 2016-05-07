package com.semdog.spacerace.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Art {
	private static HashMap<String, Texture> artwork;

	static {
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
    }

	private static Texture createPixel(Color color) {
		Pixmap pix = new Pixmap(1, 1, Format.RGB565);
		pix.setColor(color);
		pix.drawPixel(0, 0);
		return new Texture(pix);
	}

	public static Texture get(String name) {
		if(artwork.containsKey(name)) {
			return artwork.get(name);
		} else {
			try {
				Texture texture = new Texture(Gdx.files.internal("assets/graphics/" + name + ".png"));
				artwork.put(name, texture);
				return texture;
			} catch(Exception e) {
				System.err.println("Error loading your texture! Here's an error one instead.");
				return artwork.get("error");
			}
		}
	}
}
