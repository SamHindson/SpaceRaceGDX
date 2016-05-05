package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.semdog.spacerace.RaceGame;

public class BlankScren extends RaceScreen {

	public BlankScren(RaceGame game) {
		super(game);
		
		JsonReader jsonReader = new JsonReader();
		JsonValue data = jsonReader.parse(Gdx.files.internal("data/testlevel.json"));
		
		System.out.println("Done");
	}

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
