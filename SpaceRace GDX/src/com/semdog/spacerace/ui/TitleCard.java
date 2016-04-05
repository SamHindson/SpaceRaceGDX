package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.graphics.Colors;

public class TitleCard implements Disposable {

	private BitmapFont titleFont;
	
	private float x, y;
	private int size;
	
	public static final int BIG = 1, SMALL = 0;
	
	public TitleCard(int size, float x, float y) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size == 0 ? 36 : 48;
		titleFont = generator.generateFont(parameter);
		generator.dispose();
		
		this.x = x;
		this.y = y;
		this.size = size;
	}
	
	public void draw(SpriteBatch batch) {
		int k = 0;
		int m = 0;
		
		if(size == 0) {
			k = 20;
			m = 18;
		} else {
			k = 26;
		}
		
		titleFont.setColor(Colors.PLANETRED);
		titleFont.draw(batch, "Space", x - 150, y + k);
		titleFont.setColor(Colors.PLANETYELLOW);
		titleFont.draw(batch, "Race!", x - 78 - m, y - k);
	}

	@Override
	public void dispose() {
		
	}
}
