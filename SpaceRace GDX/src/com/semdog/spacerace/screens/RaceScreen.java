package com.semdog.spacerace.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.semdog.spacerace.RaceGame;

public abstract class RaceScreen extends ScreenAdapter {
	
	protected RaceGame game;
	
	public RaceScreen(RaceGame game) {
		this.game = game;
	}
	
	public abstract void update(float dt);
	public abstract void render();
	public abstract void dispose();
}
