package com.semdog.spacerace.graphics;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

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
