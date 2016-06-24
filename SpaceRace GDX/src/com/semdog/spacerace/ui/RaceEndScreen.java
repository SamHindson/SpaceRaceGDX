package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Colors;

public class RaceEndScreen extends Overlay {

	private Button retry, exit;

	public RaceEndScreen() {
		super();
		retry = new Button("Retry", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.41f, 200, 75, null);
		exit = new Button("Exit", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.3f, 200, 75, null);

		retry.setColors(Colors.PLANETBLUE, Color.WHITE);
		exit.setColors(Colors.PLANETYELLOW, Color.WHITE);

		showing = false;
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		if (showing) {
			spriteBatch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			retry.draw(spriteBatch);
			exit.draw(spriteBatch);

			titleFont.draw(spriteBatch, title, Gdx.graphics.getWidth() / 2 - titleOff,
					Gdx.graphics.getHeight() * 0.65f);
			subtitleFont.draw(spriteBatch, subtitle, Gdx.graphics.getWidth() / 2 - subtitleOff,
					Gdx.graphics.getHeight() * 0.55f);
		}
	}

	@Override
	public void update(float dt) {
		if (showing) {
			super.update(dt);
			retry.update(dt);
			exit.update(dt);
		}
	}
}
