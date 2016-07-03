package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.universe.Universe;

public class RaceEndScreen extends Overlay {

	private Button retry, exit;
	private boolean entering;

	private float age;
	private boolean titleShowing, subtitleShowing, buttonsShowing;

	public RaceEndScreen() {
		super();
        retry = new Button("Retry", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.4f, 200, 60, Universe::reset);
        exit = new Button("Exit", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.4f - 61, 200, 60, Universe::transcend);

        retry.setColors(Colors.UI_VERMILLION, Colors.UI_WHITE);
        exit.setColors(Colors.UI_TEAL, Colors.UI_WHITE);

		showing = false;
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
			titleFont.draw(spriteBatch, title, Gdx.graphics.getWidth() / 2 - titleOff,
					Gdx.graphics.getHeight() * 0.65f);

		if (subtitleShowing)
			subtitleFont.draw(spriteBatch, subtitle, Gdx.graphics.getWidth() / 2 - subtitleOff,
                    Gdx.graphics.getHeight() * 0.54f);
    }

	@Override
	public void update(float dt) {
		if (entering) {
			age += dt;

			if (age > 0) {
				titleShowing = true;
			}
			if (age > 0.5f) {
				subtitleShowing = true;
			}

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
		entering = true;
		age = 0;
	}
}
