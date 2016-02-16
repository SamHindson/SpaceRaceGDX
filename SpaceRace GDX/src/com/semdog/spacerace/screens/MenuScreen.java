package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.ui.Button;

public class MenuScreen extends RaceScreen {
	
	private SpriteBatch batch;
	
	private BitmapFont titleFont;
	private float titleOffset;
	
	private Button button1, button2, button3;

	public MenuScreen(RaceGame game) {
		super(game);
		
		batch = new SpriteBatch();
		titleFont = new BitmapFont(Gdx.files.internal("assets/fonts/VCR64.fnt"));
		
		GlyphLayout titleLayout = new GlyphLayout(titleFont, "SPACERACE");
		titleOffset = titleLayout.width / 2;
		
		button1 = new Button("LET'S RACE!", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.5f, 200, 50, () -> {
			game.changeScreen("play");
		});
		button1.setColor(Color.RED);
		
		button2 = new Button("LET'S NOT.", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.4f, 200, 50, () -> {
			System.out.println("Dogs!");
		});
		button2.setColor(Color.BLUE);
		
		button3 = new Button("I'M LEAVING.", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.3f, 200, 50, () -> {
			Gdx.app.exit();
		});
		button3.setColor(Color.ORANGE);
	}

	@Override
	public void update(float dt) {
		button1.update(dt);
		button2.update(dt);
		button3.update(dt);
	}

	@Override
	public void render() {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glClearColor(0, 0, 0, 1f);

		batch.begin();
		
		for(int k = 0; k < 10; k++) {
			titleFont.setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 0.5f);
			titleFont.draw(batch, "SPACERACE", Gdx.graphics.getWidth() / 2 - titleOffset + (MathUtils.random(-5, 5)), Gdx.graphics.getHeight() * 0.8f + (MathUtils.random(-5, 5)));
		}

		titleFont.setColor(Color.WHITE);
		titleFont.draw(batch, "SPACERACE", Gdx.graphics.getWidth() / 2 - titleOffset, Gdx.graphics.getHeight() * 0.8f);
		
		button1.draw(batch);
		button2.draw(batch);
		button3.draw(batch);
		
		batch.end();
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		titleFont.dispose();
	}
	
}
