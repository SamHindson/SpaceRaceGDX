package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.semdog.spacerace.graphics.Art;

/**
 * @author Sam TODO write about this
 */

public abstract class Overlay {
	protected boolean showing;
	protected boolean escapable;

	protected BitmapFont titleFont, subtitleFont;
	protected String title, subtitle;
	protected float titleOff, subtitleOff;

	protected Texture background;

	public Overlay() {
		background = Art.get("pixel_white");

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/Mohave.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 84;
        titleFont = generator.generateFont(parameter);
        parameter.size = 48;
        subtitleFont = generator.generateFont(parameter);

		title = subtitle = "";
	}

	public void update(float dt) {
		if (showing) {
			if (escapable) {
				if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
					showing = false;
				}
			}
		}
	}

	public abstract void draw(SpriteBatch spriteBatch);

	public void setShowing(boolean showing) {
		this.showing = showing;
	}

	public void setText(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;

		GlyphLayout titleGlyphs = new GlyphLayout(titleFont, title);
		GlyphLayout subtitleGlyphs = new GlyphLayout(subtitleFont, subtitle);

		titleOff = titleGlyphs.width / 2;
		subtitleOff = subtitleGlyphs.width / 2;
	}
}
