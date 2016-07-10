package com.semdog.spacerace.misc;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

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
	
	private static void load(String name) {
		FileHandle fileHandle = Gdx.files.internal("assets/fonts/" + name + ".fnt");
		BitmapFont font = new BitmapFont(fileHandle);
		fonts.put(name, font);
	}
	
	public static BitmapFont getFont(String name) {
		if(fonts.containsKey(name))
			return fonts.get(name);
		else {
			Gdx.app.error("FontManager", "Font " + name + " was requested, but was not found!");
			return fonts.get("fipps-12");
		}
			
	}
}
