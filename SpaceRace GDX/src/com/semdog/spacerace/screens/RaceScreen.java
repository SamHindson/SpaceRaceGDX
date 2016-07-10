package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.misc.FontManager;

public abstract class RaceScreen extends ScreenAdapter {
	
	protected RaceGame game;
	
	protected BitmapFont titleFont;
	protected String title;
	protected float titleX, titleY;

    private boolean markedForDestruction = false;

    public RaceScreen(RaceGame game) {
		this.game = game;
	}
	
	protected void setTitle(String title) {
		titleFont = FontManager.getFont("fipps-24");
		
		this.title = title;
		
		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(titleFont, title);
		titleX = Gdx.graphics.getWidth() / 2 - glyphLayout.width / 2;
        titleY = Gdx.graphics.getHeight() * 0.9f + glyphLayout.height / 2;
    }
	
	protected void drawTitle(SpriteBatch batch) {
        titleFont.draw(batch, title, Gdx.graphics.getWidth() / 5, titleY, Gdx.graphics.getWidth() * 0.6f, 1, true);
    }
	
	public abstract void update(float dt);
	public abstract void render();

    public void dispose() {
        markedForDestruction = true;
    }

    public boolean isMarkedForDestruction() {
        return markedForDestruction;
    }

    public void setMarkedForDestruction(boolean markedForDestruction) {
        this.markedForDestruction = markedForDestruction;
    }

    public RaceGame getGame() {
        return game;
    }
}
