package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.net.scoring.MetaPlayer;

/**
 * A class which allows to be able to draw over the current screen for any
 * reason. Not as powerful as the Notification class, but infinitely more
 * extendible.
 * 
 * @author Sam
 */

public abstract class Overlay {
	protected boolean showing;
	protected boolean escapable;

	protected String title, subtitle;
	protected Texture background;
	protected Color color;

	public Overlay() {
		background = Art.get("pixel_white");
		title = subtitle = "";
	}

	public void update(float dt) {
		if (showing) 
			if (escapable) 
				if (Gdx.input.isKeyPressed(Keys.ESCAPE)) 
					showing = false;
	}

	public void draw(SpriteBatch spriteBatch) {
		if (showing) {
			spriteBatch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	public void setShowing(boolean showing) {
		this.showing = showing;
	}

	public void setText(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
