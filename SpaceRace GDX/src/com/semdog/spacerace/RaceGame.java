package com.semdog.spacerace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.screens.MenuScreen;
import com.semdog.spacerace.screens.PlayScreen;
import com.semdog.spacerace.screens.RaceScreen;
import com.semdog.spacerace.screens.SingleplayerMenu;

public class RaceGame extends ApplicationAdapter {

    // TEST FEATURES
    FrameBuffer frameBuffer;
    SpriteBatch frameBufferBatch;
    ShaderProgram shaderProgram;
    private RaceScreen screen;
	
	@Override
	public void create () {
		Gdx.app.log("RaceGame", "Welcome! Loading bits and pieces...");
		Art.initialize();
		Gdx.app.log("RaceGame", "Loaded Graphics...");
		SoundManager.initialize();		
		Gdx.app.log("RaceGame", "Loaded Audio...");
		Gdx.app.log("RaceGame", "Finisce!");
		screen = new PlayScreen(this);

        frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, (int) (Gdx.graphics.getWidth() / 1.25),
                (int) (Gdx.graphics.getHeight() / 1.25), true);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        frameBufferBatch = new SpriteBatch();

        shaderProgram = new ShaderProgram(Gdx.files.internal("assets/shaders/shader.vsh"),
                Gdx.files.internal("assets/shaders/shader.fsh"));
        System.out.println(shaderProgram.isCompiled() ? "Yeah!" : shaderProgram.getLog());
        frameBufferBatch.setShader(shaderProgram);
        ShaderProgram.pedantic = false;

	}

	@Override
	public void render () {
		screen.update(Gdx.graphics.getDeltaTime());

        //shaderProgram.begin();
        //shaderProgram.setUniformf("f_ringy", MathUtils.random(0.9f, 1f));
        //shaderProgram.end();

        frameBuffer.begin();
        screen.render();
        frameBuffer.end();

        frameBufferBatch.begin();
        frameBufferBatch.draw(frameBuffer.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(),
                -Gdx.graphics.getHeight());
        frameBufferBatch.end();
    }

	public void changeScreen(String name) {
		if(name.equals("play")) {
			screen.dispose();
			screen = new PlayScreen(this);
		} else if(name.equals("menu")) {
			screen.dispose();
			screen = new MenuScreen(this);
		} else if(name.equals("playmenu")) {
			screen.dispose();
			screen = new SingleplayerMenu(this);
		}
	}
}