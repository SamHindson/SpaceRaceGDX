package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.races.Race;

public class RaceInfoViewer {
	private Race race;
	
	private String title, description;
	private float yourTime, bestTime;
	
	private BitmapFont titleFont, descriptionFont, timeFont;
	private Color borderColor;
	private Button button;
	private Texture block;
	
	private float x, y, width, height;
	
	public RaceInfoViewer(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		parameter.size = 24;
		titleFont = generator.generateFont(parameter);

		parameter.size = 16;
		descriptionFont = generator.generateFont(parameter);
		generator.dispose();
		
		block = Art.get("pixel_white");
		
		borderColor = Colors.P_BLUE;
	}
	
	public void draw(SpriteBatch batch) {
		batch.setColor(borderColor);
		batch.draw(block, x, y, width, height);
		batch.setColor(Color.BLACK);
		batch.draw(block, x + 5, y + 5, width - 10, height - 10);
	}
}
