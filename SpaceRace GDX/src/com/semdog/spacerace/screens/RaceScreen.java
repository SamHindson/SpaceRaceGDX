package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.misc.FontManager;

/**
 * An abstract class which all screens inherit from.
 * 
 * @author Sam
 */

public abstract class RaceScreen extends ScreenAdapter {

	protected RaceGame game; // A reference to the game that created it
	protected BitmapFont titleFont; // The font for drawing the title
	protected String title; // The title

	private boolean markedForDestruction = false; // Flag for whether the screen should terminate on the next tick

	public RaceScreen(RaceGame game) {
		this.game = game;
		titleFont = FontManager.getFont("fipps-24");
	}

	public abstract void update(float dt); // The method where all logic goes

	public abstract void render(); // The method where all rendering goes

	@Override
	public void dispose() {
		super.dispose();
		markedForDestruction = true;
	}

	public abstract void exit(); // What the screen is to do when it exits

	public RaceGame getGame() {
		return game;
	}

	protected void setTitle(String title) {
		this.title = title;
	}

	protected void drawTitle(SpriteBatch batch) {
		titleFont.draw(batch, title, Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() * 0.9f, Gdx.graphics.getWidth() * 0.6f, 1, true);
	}

	public boolean isMarkedForDestruction() {
		return markedForDestruction;
	}

	public void setMarkedForDestruction(boolean markedForDestruction) {
		this.markedForDestruction = markedForDestruction;
	}
}
