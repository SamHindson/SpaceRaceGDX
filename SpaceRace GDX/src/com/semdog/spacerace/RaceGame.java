package com.semdog.spacerace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.screens.MenuScreen;
import com.semdog.spacerace.screens.PlayScreen;
import com.semdog.spacerace.screens.RaceScreen;
import com.semdog.spacerace.screens.SingleplayerMenu;

public class RaceGame extends ApplicationAdapter {

    private static Pixmap cursor, crosshair;
    // TEST FEATURES
    FrameBuffer frameBuffer;
    SpriteBatch frameBufferBatch;
    ShaderProgram shaderProgram;
    ShapeRenderer backgroundRenderer;
    Array<BackgroundElement> backgroundElements;
    private RaceScreen screen;

    public static void enableCrosshair() {
        Gdx.app.log("RaceGame", "Enabled crosshairs!");
        Gdx.input.setCursorImage(crosshair, 4, 4);
    }

    public static void enableCursor() {
        Gdx.input.setCursorImage(cursor, 4, 4);
    }

    @Override
    public void create() {
        Gdx.app.log("RaceGame", "Welcome! Loading bits and pieces...");
        Art.initialize();
        Gdx.app.log("RaceGame", "Loaded Graphics...");
        SoundManager.initialize();
        Gdx.app.log("RaceGame", "Loaded Audio...");
        Gdx.app.log("RaceGame", "Finisce!");

        backgroundRenderer = new ShapeRenderer();
        backgroundElements = new Array<>();

        for (int w = 0; w < 1000; w++) {
            backgroundElements.add(new BackgroundElement());
        }

        Pixmap cursor = new Pixmap(Gdx.files.internal("assets/graphics/cursor.png"));
        Pixmap crosshair = new Pixmap(Gdx.files.internal("assets/graphics/crosshair.png"));

        Gdx.input.setCursorImage(cursor, 4, 4);
        //enableCursor();

        screen = new MenuScreen(this);

        frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, (int) (Gdx.graphics.getWidth() / 2),
                (int) (Gdx.graphics.getHeight() / 2), true);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        frameBufferBatch = new SpriteBatch();

        shaderProgram = new ShaderProgram(Gdx.files.internal("assets/shaders/shader.vsh"),
                Gdx.files.internal("assets/shaders/shader.fsh"));

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("RaceGame", "There's a problem with your shader!");
            System.err.println(shaderProgram.getLog());
        }

        ShaderProgram.pedantic = false;

        frameBufferBatch.setShader(shaderProgram);
    }

    @Override
    public void render() {
        if (screen.isMarkedForDestruction())
            return;

        if (!(screen instanceof PlayScreen)) {
            for (BackgroundElement element : backgroundElements) {
                element.update(Gdx.graphics.getDeltaTime());
            }
        }

        screen.update(Gdx.graphics.getDeltaTime());

        SoundManager.update();

        frameBuffer.begin();


        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
                | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        Gdx.gl20.glClearColor(0, 0, 0, 0.05f);
        if (!(screen instanceof PlayScreen)) {
            backgroundRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (BackgroundElement element : backgroundElements) {
                backgroundRenderer.setColor(element.color);
                backgroundRenderer.circle(element.x, element.y, element.radius);
            }
            backgroundRenderer.end();
            for (BackgroundElement element : backgroundElements) {
                element.update(Gdx.graphics.getDeltaTime());
            }
        }
        screen.render();
        frameBuffer.end();

        frameBufferBatch.begin();
        frameBufferBatch.draw(frameBuffer.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(),
                -Gdx.graphics.getHeight());
        frameBufferBatch.end();
    }

    public void changeScreen(String name) {
        if (!name.equals("play"))
            enableCursor();

        switch (name) {
            case "play":
                screen.dispose();
                screen = new PlayScreen(this);
                SoundManager.stopMusic("menu");
                break;
            case "menu":
                screen.dispose();
                screen = new MenuScreen(this);
                Gdx.app.log("RaceGame", screen.toString());
                break;
            case "playmenu":
                screen.dispose();
                screen = new SingleplayerMenu(this);
                break;
        }
    }
}

class BackgroundElement {
    float x, y, radius, dy;
    Color color;

    BackgroundElement() {
        y = MathUtils.random(Gdx.graphics.getHeight());
        reset();
    }

    private void reset() {
        x = MathUtils.random(Gdx.graphics.getWidth());
        dy = MathUtils.random(50);

        if (MathUtils.randomBoolean(0.001f)) {
            color = new Color(Colors.getRandom()).mul(0.7f);
            radius = MathUtils.random(5, 20);
        } else {
            float e = MathUtils.random() * 0.7f;
            color = new Color(e, e, e, 1);
            radius = MathUtils.random(0.5f, 1.1f);
        }
    }

    void update(float dt) {
        y += dy * dt;

        if (y > Gdx.graphics.getHeight() + radius) {
            reset();
            y = -radius;
        }
    }
}