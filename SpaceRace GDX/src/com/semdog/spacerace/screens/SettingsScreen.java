package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.ui.TitleCard;

public class SettingsScreen extends RaceScreen {
	
	private SpriteBatch batch;
	private TitleCard titleCard;

	public SettingsScreen(RaceGame game) {
		super(game);
		batch = new SpriteBatch();
		titleCard = new TitleCard(0, 160, Gdx.graphics.getHeight() * 0.92f);
	}

	@Override
	public void update(float dt) {
		
	}

	@Override
	public void render() {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glClearColor(0, 0, 0, 1f);
		batch.begin();
		titleCard.draw(batch);
		batch.end();
	}

	@Override
	public void dispose() {
		
	}

}
