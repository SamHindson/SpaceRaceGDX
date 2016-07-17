package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.universe.Universe;

/**
 * An overlay which shows when a race has ended either successfully or
 * otherwise.
 * 
 * Only shows after a delay so as to line the motions up with the music.
 * 
 * @author Sam
 */

public class RaceEndScreen extends Overlay {

	private boolean entering;
	private boolean titleShowing, subtitleShowing, buttonsShowing;
	private float age;
	private BitmapFont titleFont, subtitleFont;
	private Button retry, exit;

	public RaceEndScreen() {
		super();
		retry = new Button("Retry", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.2f + 30, 200, 30, Universe::reset);
		exit = new Button("Exit", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.2f, 200, 30, Universe::transcend);

		titleFont = FontManager.getFont("mohave-84-bold");
		subtitleFont = FontManager.getFont("mohave-48-bold");

		retry.setColors(Color.CLEAR, Colors.UI_WHITE);
		exit.setColors(Color.CLEAR, Colors.UI_WHITE);

		showing = false;

		color = Colors.UI_WHITE;
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		if (entering || showing) {
			spriteBatch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}

		if (buttonsShowing) {
			retry.draw(spriteBatch);
			exit.draw(spriteBatch);
		}

		if (titleShowing)
			titleFont.draw(spriteBatch, title, 0, Gdx.graphics.getHeight() * 0.6f, Gdx.graphics.getWidth(), 1, false);

		if (subtitleShowing) {
			subtitleFont.setColor(color);
			subtitleFont.draw(spriteBatch, subtitle, 0, Gdx.graphics.getHeight() * 0.5f, Gdx.graphics.getWidth(), 1, true);
		}
	}

	@Override
	public void update(float dt) {
		if (entering) {
			age += dt;

			if (age > 0)
				titleShowing = true;

			if (age > 0.5f)
				subtitleShowing = true;

			if (age > 4f) {
				buttonsShowing = true;
				showing = true;
				entering = false;
			}
		}

		if (showing) {
			super.update(dt);
			retry.update(dt);
			exit.update(dt);
		}
	}

	@Override
	public void setShowing(boolean showing) {
		super.setShowing(showing);
		entering = true;
		age = 0;
	}
}
