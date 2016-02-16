package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.universe.Universe;

public class PlayScreen extends RaceScreen {
	
	private Universe universe;

	public PlayScreen(RaceGame game) {
		super(game);
		
		universe = new Universe();
	}

	@Override
	public void update(float dt) {
		universe.tick(dt);
	}

	@Override
	public void render() {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glClearColor(0, 0, 0, 1f);
		
		universe.render();
	}

	@Override
	public void dispose() {

	}

}
