package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.misc.FontManager;

/**
 * @author Sam TODO write about this
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
	}

    public void setColor(Color color) {
        this.color = color;
    }
}
