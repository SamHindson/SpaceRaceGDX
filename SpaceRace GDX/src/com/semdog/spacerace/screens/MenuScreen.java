package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.ui.Button;

public class MenuScreen extends RaceScreen {
	
	//TODO fix the horrendous button colors
	
	private SpriteBatch batch;
	
	private BitmapFont titleFont;
	private float titleOffset;
	
	private Button button1, button2, button3, button4, button5, secretButton;

	public MenuScreen(RaceGame game) {
		super(game);
		
		batch = new SpriteBatch();
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 48;
		
		titleFont = generator.generateFont(parameter);
		generator.dispose();
		
		button1 = new Button("Singleplayer", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.55f, 200, 50, () -> {
			game.changeScreen("play");
		});
		button1.setColors(Colors.PLANETYELLOW, Color.BLUE);
		
		button2 = new Button("Multiplayer", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.45f, 200, 50, () -> {
			System.out.println("Dogs!");
		});
		button2.setColors(Colors.PLANETBLUE, Color.YELLOW);
		
		button3 = new Button("Settings", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.35f, 200, 50, () -> {
			System.out.println("Settings");
		});
		button3.setColors(Colors.PLANETORANGE, Color.GREEN);
		
		button4 = new Button("Help", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.25f, 200, 50, () -> {
			System.out.println("Halp");
		});
		button4.setColors(Colors.PLANETPINK, Color.WHITE);
		
		button5 = new Button("Quit", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.15f, 200, 50, () -> {
			Gdx.app.exit();
		});
		button5.setColors(Colors.PLANETGREEN, Color.DARK_GRAY);
		
		secretButton = new Button("???", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.9f, 400, 300, () -> {
			Gdx.app.exit();
		});
		secretButton.setColors(Color.ORANGE, Color.GREEN);
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
		
		titleFont.setColor(Colors.PLANETRED);
		titleFont.draw(batch, "Space", Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() * 0.95f);
		titleFont.setColor(Colors.PLANETYELLOW);
		titleFont.draw(batch, "Race!", Gdx.graphics.getWidth() / 2 - 78, Gdx.graphics.getHeight() * 0.85f);
		
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
		titleFont.dispose();
	}
	
}
