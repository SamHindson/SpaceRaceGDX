package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		Gdx.gl20.glClearColor(0, 0, 0, 1f);
		universe.render();
		
		universe.finalizeState();
	}

	@Override
	public void dispose() {

	}

}
