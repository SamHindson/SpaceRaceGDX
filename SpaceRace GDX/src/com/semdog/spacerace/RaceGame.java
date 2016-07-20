package com.semdog.spacerace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.screens.BugReportScreen;
import com.semdog.spacerace.screens.HelpScreen;
import com.semdog.spacerace.screens.KeysScreen;
import com.semdog.spacerace.screens.MenuScreen;
import com.semdog.spacerace.screens.MultiplayerMenu;
import com.semdog.spacerace.screens.PlayScreen;
import com.semdog.spacerace.screens.RaceScreen;
import com.semdog.spacerace.screens.SettingsScreen;
import com.semdog.spacerace.screens.SingleplayerMenu;
import com.semdog.spacerace.screens.ThankYouScreen;
import com.semdog.spacerace.ui.HelpSection;
import com.semdog.spacerace.ui.Notification;

/**
 * The main class that handles all the screens, notifications and loading of
 * resources.
 */

public class RaceGame extends ApplicationAdapter {

    private ShapeRenderer backgroundRenderer;
    private Array<BackgroundElement> backgroundElements;
    private RaceScreen screen;
    
    private FrameBuffer frameBuffer;
    private SpriteBatch mainBatch;
    private ShaderProgram shader;

    @Override
    public void create() {
        FontManager.initialize();
        SoundManager.initialize();
        SettingsManager.initialize();
        Art.initialize();
        HelpSection.initialize();
        RaceManager.initialize();

        Gdx.graphics.setDisplayMode(SettingsManager.getWidth(), SettingsManager.getHeight(), SettingsManager.isFullscreen());

        backgroundRenderer = new ShapeRenderer();
        backgroundElements = new Array<>();

        for (int w = 0; w < 1000; w++) {
            backgroundElements.add(new BackgroundElement());
        }

        Pixmap cursor = new Pixmap(Gdx.files.internal("assets/graphics/cursor.png"));
        Gdx.input.setCursorImage(cursor, 0, 0);

        screen = new MenuScreen(this);

        if (SettingsManager.isFirstTime()) {
            Notification.show("Welcome to SpaceRace!\nDespite its cute looks, this game is quite literally rocket science and thus the help section is highly recommended.", "Take Me There", "I'm Smart", Colors.P_PINK, Colors.P_BLUE, () -> {
                changeScreen("help");
                Notification.showing = false;
            }, () -> Notification.showing = false);

            SettingsManager.setFirstTime(false);
            SettingsManager.writeSettings();
        }
        
        frameBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        frameBuffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
      
        mainBatch = new SpriteBatch();
        
        shader = new ShaderProgram(Gdx.files.internal("assets/shaders/shader.vsh"), Gdx.files.internal("assets/shaders/shader.fsh"));
        ShaderProgram.pedantic = true;
        if(!shader.isCompiled())
        {
        	System.out.println("Welp! DId not compoiule");
        	System.err.println(shader.getLog());
        	System.exit(-5);
        }
        mainBatch.setShader(shader);
    }

    @Override
    public void render() {
        if (screen.isMarkedForDestruction())
            return;

        if (!(screen instanceof PlayScreen))
            for (BackgroundElement element : backgroundElements) {
                element.update(Gdx.graphics.getDeltaTime());
            }

        screen.update(Gdx.graphics.getDeltaTime());
        Notification.update(Gdx.graphics.getDeltaTime());

        SoundManager.update();
        
        frameBuffer.begin();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        Gdx.gl20.glClearColor(0, 0, 0, 1f);
        if (!(screen instanceof PlayScreen)) {
            backgroundRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (BackgroundElement element : backgroundElements) {
                backgroundRenderer.setColor(element.color);
                backgroundRenderer.circle(element.x, element.y, element.radius);
            }
            backgroundRenderer.end();
        }
        screen.render();
        Notification.draw();
        frameBuffer.end();
        
        mainBatch.begin(); 
        mainBatch.setShader(shader);
        mainBatch.draw(frameBuffer.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        mainBatch.end();
    }

    public void changeScreen(String name) {
        switch (name) {
            case "play":
                screen.dispose();
                screen = new PlayScreen(this, false);
                SoundManager.stopMusic("menu");
                break;
            case "multiplay":
                screen.dispose();
                screen = new PlayScreen(this, true);
                SoundManager.stopMusic("menu");
                break;
            case "menu":
                screen.dispose();
                screen = new MenuScreen(this);
                break;
            case "playmenu":
                screen.dispose();
                screen = new SingleplayerMenu(this);
                break;
            case "settings":
                screen.dispose();
                screen = new SettingsScreen(this);
                break;
            case "keys":
                screen.dispose();
                screen = new KeysScreen(this);
                break;
            case "thankyou":
                screen.dispose();
                screen = new ThankYouScreen(this);
                break;
            case "help":
                screen.dispose();
                screen = new HelpScreen(this);
                break;
            case "bug":
                screen.dispose();
                screen = new BugReportScreen(this);
                break;
            case "multiplaymenu":
                screen.dispose();
                screen = new MultiplayerMenu(this);
                break;
        }

        if (!name.equals("play") && !SoundManager.isMusicPlaying("menu"))
            SoundManager.playMusic("menu", true);
    }
}

class BackgroundElement {
    float x;
    float y;
    float radius;
    Color color;
    private float dy;

    BackgroundElement() {
        y = MathUtils.random(Gdx.graphics.getHeight());
        reset();
    }

    private void reset() {
        x = MathUtils.random(Gdx.graphics.getWidth());
        dy = MathUtils.random(50);

        if (MathUtils.randomBoolean(0.001f)) {
            color = new Color(Colors.getRandomPlanetColor()).mul(0.7f);
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