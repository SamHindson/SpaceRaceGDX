package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
        retry = new Button("Retry", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.2f + 60, 200, 30, Universe::reset);
        exit = new Button("Exit", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.2f, 200, 30, Universe::transcend);

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
			titleFont.draw(spriteBatch, title, Gdx.graphics.getWidth() / 2 - titleOff,
                    Gdx.graphics.getHeight() * 0.6f);

        if (subtitleShowing) {
            subtitleFont.setColor(color);
            subtitleFont.draw(spriteBatch, subtitle, 0, Gdx.graphics.getHeight() * 0.5f, Gdx.graphics.getWidth(), 1, false);
        }
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

    @Override
    public void setText(String title, String subtitle) {
        super.setText(title, subtitle);
        if (subtitle.contains("Record")) {
            retry.setPosition(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() * 0.5f) - (subtitleFont.getLineHeight() * 2f));
            exit.setPosition(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() * 0.5f) - (subtitleFont.getLineHeight() * 2f) - 30);
        } else {
            retry.setPosition(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() * 0.5f) - (subtitleFont.getCapHeight() * 1.5f));
            exit.setPosition(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() * 0.5f) - (subtitleFont.getCapHeight() * 1.5f) - 30);
        }
    }
}
