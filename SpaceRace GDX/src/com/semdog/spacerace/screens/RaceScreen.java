package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.semdog.spacerace.RaceGame;

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
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 24;
		titleFont = generator.generateFont(parameter);
		generator.dispose();
		
		this.title = title;
		
		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(titleFont, title);
		titleX = Gdx.graphics.getWidth() / 2 - glyphLayout.width / 2;
		titleY = Gdx.graphics.getHeight() * 0.8f + glyphLayout.height / 2;
	}
	
	protected void drawTitle(SpriteBatch batch) {
		titleFont.draw(batch, title, titleX, titleY);
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
