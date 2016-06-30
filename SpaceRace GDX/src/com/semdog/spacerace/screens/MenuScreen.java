package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.TitleCard;

public class MenuScreen extends RaceScreen {
	
	//TODO fix the horrendous button colors
	
	private SpriteBatch batch;
	
	//private float titleOffset;
	
	private Button button1, button2, button3, button4, button5, secretButton;
	private TitleCard titleCard;

	public MenuScreen(RaceGame game) {
		super(game);
		
		batch = new SpriteBatch();
		
		button1 = new Button("Singleplayer", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.55f, 200, 50, () -> {
			game.changeScreen("playmenu");
		});
		button1.setColors(Colors.P_YELLOW, Color.BLUE);
		
		button2 = new Button("Multiplayer", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.48f, 200, 50, () -> {
			System.out.println("Dogs!");
		});
		button2.setColors(Colors.P_BLUE, Color.YELLOW);
		
		button3 = new Button("Settings", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.41f, 200, 50, () -> {
			game.changeScreen("settings");
		});
		button3.setColors(Colors.P_ORANGE, Color.GREEN);
		
		button4 = new Button("Help", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.34f, 200, 50, () -> {
			System.out.println("Halp");
		});
		button4.setColors(Colors.P_PINK, Color.WHITE);
		
		button5 = new Button("Quit", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.27f, 200, 50, () -> {
			Gdx.app.exit();
		});
		button5.setColors(Colors.P_GREEN, Color.DARK_GRAY);
		
		secretButton = new Button("???", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.9f, 400, 300, () -> {
			Gdx.app.exit();
		});
		secretButton.setColors(Color.ORANGE, Color.GREEN);
		
		titleCard = new TitleCard(1, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.85f);
	}

	@Override
	public void update(float dt) {
		button1.update(dt);
		button2.update(dt);
		button3.update(dt);
		button4.update(dt);
		button5.update(dt);
		secretButton.update(dt);
	}

	@Override
	public void render() {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glClearColor(0, 0, 0, 1f);

		batch.begin();
		
		titleCard.draw(batch);
		
		button1.draw(batch);
		button2.draw(batch);
		button3.draw(batch);
		button4.draw(batch);
		button5.draw(batch);
		
		batch.end();
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		titleCard.dispose();
	}
	
}
