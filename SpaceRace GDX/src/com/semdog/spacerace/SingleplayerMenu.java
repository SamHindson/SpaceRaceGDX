package com.semdog.spacerace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.screens.RaceScreen;
import com.semdog.spacerace.ui.ListView;
import com.semdog.spacerace.ui.TitleCard;

public class SingleplayerMenu extends RaceScreen {

	private SpriteBatch batch;

	private TitleCard titleCard;

	private BitmapFont subtitleFont;

	private ListView raceChooser;

	public SingleplayerMenu(RaceGame game) {
		super(game);

		titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 18;
		subtitleFont = generator.generateFont(parameter);
		generator.dispose();

		setTitle("Select a Race!");

		batch = new SpriteBatch();

		raceChooser = new ListView(100, 100, 250, 420, new Color(1.f, 0, 110f / 255f, 1),
				new Color(178f / 255f, 0, 1, 1), 5);
		
	}

	@Override
	public void update(float dt) {
		raceChooser.update(dt);
	}

	@Override
	public void render() {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glClearColor(0, 0, 0, 1f);

		batch.begin();
		titleCard.draw(batch);

		subtitleFont.setColor(Colors.PLANETBLUE);
		subtitleFont.draw(batch, "Singleplayer", 100, Gdx.graphics.getHeight() - 90);

		drawTitle(batch);

		raceChooser.draw(batch);
		batch.end();
	}

	@Override
	public void dispose() {

	}

}
