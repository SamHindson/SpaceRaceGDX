package com.semdog.spacerace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.effects.Curvature;
import com.bitfire.postprocessing.filters.CrtScreen;
import com.bitfire.utils.ShaderLoader;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.screens.*;
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

    private PostProcessor postProcessor;
    private CrtMonitor crtMonitor;

    private float age;
    private float screenChangeJitter = 0;
    private boolean exiting = false;

    /**
     * The first method called after main()
     */
    @Override
    public void create() {
        /* Initializing the managers */
        FontManager.initialize();
        SoundManager.initialize();
        SettingsManager.initialize();
        Art.initialize();
        HelpSection.initialize();
        RaceManager.initialize();

        /* Setting correct resolution */
        Gdx.graphics.setWindowedMode(SettingsManager.getWidth(), SettingsManager.getHeight());

        if (SettingsManager.isFullscreen())
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        /* Setting up the stars in the background */
        backgroundRenderer = new ShapeRenderer();
        backgroundElements = new Array<>();
        for (int w = 0; w < 1000; w++) {
            backgroundElements.add(new BackgroundElement());
        }

        /* Sets the custom cursor */
        Pixmap cursor = new Pixmap(Gdx.files.internal("assets/graphics/cursor.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor, 0, 0));

        /* Initializes the screen */
        screen = new MenuScreen(this);

        /* Welcomes the player if it is their first time playing */
        if (SettingsManager.isFirstTime()) {
            Notification.show("Welcome to SpaceRace!\nDespite its cute looks, this game is quite literally rocket science and thus the help section is highly recommended.", "Take Me There", "I'm Smart", Colors.P_PINK, Colors.P_BLUE, () -> {
                changeScreen("help");
                Notification.showing = false;
            }, () -> Notification.showing = false);

            SettingsManager.setFirstTime(false);
            SettingsManager.writeSettings();
        }

        /* Sets up the post processor */
        ShaderLoader.BasePath = "assets/shaders/";
        postProcessor = new PostProcessor(false, false, true);

        int effects = CrtScreen.Effect.Scanlines.v | CrtScreen.Effect.PhosphorVibrance.v;
        crtMonitor = new CrtMonitor(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false, CrtScreen.RgbMode.RgbShift, effects);
        crtMonitor.setColorOffset(0.001f);
        postProcessor.addEffect(crtMonitor);

        Curvature curvature = new Curvature();
        curvature.setDistortion(0.2f);
        postProcessor.addEffect(curvature);

        Bloom bloom = new Bloom(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bloom.setBloomIntesity(0.1f);
        postProcessor.addEffect(bloom);
    }

    @Override
    public void render() {
        /* We will update the background stars if we are not on the play screen */
        if (!(screen instanceof PlayScreen))
            for (BackgroundElement element : backgroundElements) {
                element.update(Gdx.graphics.getDeltaTime());
            }

        /* Updating relevant objects */
        screen.update(Gdx.graphics.getDeltaTime());

        if (exiting) return;

        Notification.update(Gdx.graphics.getDeltaTime());
        SoundManager.update();

        age += Gdx.graphics.getDeltaTime();

        if (screenChangeJitter > 0.001f) {
            screenChangeJitter -= Gdx.graphics.getDeltaTime() * 10.f;
        } else {
            screenChangeJitter = 0;
        }

        if (SettingsManager.isPostprocessing()) {
            crtMonitor.setTime(age);
            float f = MathUtils.random(screenChangeJitter);
            crtMonitor.setColorOffset(0.00175f + f);

        /* Captures screen for post processing */
            postProcessor.capture();
        }

        /* Clears the screen for a render */
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT/* | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0)*/);
        Gdx.gl20.glClearColor(0, 0, 0, 1f);

        /* Draws stars if we are not busy playing */
        if (!(screen instanceof PlayScreen)) {
            backgroundRenderer.begin(ShapeRenderer.ShapeType.Filled);
            backgroundRenderer.setColor(new Color(0.05f, 0.05f, 0.06f, 1));
            backgroundRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            for (BackgroundElement element : backgroundElements) {
                backgroundRenderer.setColor(element.color);
                backgroundRenderer.circle(element.x, element.y, element.radius);
            }
            backgroundRenderer.end();
        }

        /* We draw our current screen */
        screen.render();

        /* Finally, we draw our notifications */
        Notification.draw();

        /* Finally (Part 2) we render the post-processed screen if we need to*/
        if (SettingsManager.isPostprocessing()) postProcessor.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.exit();

        screen.dispose();
        backgroundRenderer.dispose();
        postProcessor.dispose();
        Notification.dispose();
        exiting = true;
        System.exit(5);
    }

    /**
     * Sets the screen to the desired one.
     */
    public void changeScreen(String name) {
        /* Get rid of the current screen */
        screen.dispose();

        /* Adds to the color jitter effect when changing screens */
        screenChangeJitter = 0.1f;

        /* Sets the screen accordingly */
        switch (name) {
            case "play":
                screen = new PlayScreen(this, false);
                SoundManager.stopMusic("menu");
                break;
            case "multiplay":
                screen = new PlayScreen(this, true);
                SoundManager.stopMusic("menu");
                break;
            case "menu":
                screen = new MenuScreen(this);
                break;
            case "playmenu":
                screen = new SingleplayerMenu(this);
                break;
            case "settings":
                screen = new SettingsScreen(this);
                break;
            case "keys":
                screen = new KeysScreen(this);
                break;
            case "thankyou":
                screen = new ThankYouScreen(this);
                break;
            case "help":
                screen = new HelpScreen(this);
                break;
            case "bug":
                screen = new BugReportScreen(this);
                break;
            case "multiplaymenu":
                screen = new MultiplayerMenu(this);
                break;
        }

        /* Stops/starts any music that needs to do so */
        if (!name.equals("play") && !SoundManager.isMusicPlaying("menu")) {
            SoundManager.playMusic("menu", true);
            SoundManager.stopMusic("oxidiser");
        }
    }
}

/**
 * A class to define the little stars and planets you see in the menu background
 */
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