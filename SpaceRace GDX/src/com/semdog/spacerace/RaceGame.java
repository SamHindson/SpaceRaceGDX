package com.semdog.spacerace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
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
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.screens.*;
import com.semdog.spacerace.ui.HelpSection;
import com.semdog.spacerace.ui.Notification;

/**
 * The main class that handles all the screens, notifications and loading of
 * resources.
 *
 * @author sam
 */

public class RaceGame extends ApplicationAdapter {

    private ShapeRenderer backgroundRenderer;
    private Array<BackgroundElement> backgroundElements;
    private RaceScreen screen;
    private PostProcessor postProcessor;
    private CrtMonitor crtMonitor;
    private Bloom bloom;
    private SpriteBatch loadingBatch;
    private Texture trousers, pixel;
    private float age;
    private float screenChangeJitter = 0;
    private boolean exiting = false;
    private boolean firstFrame = true;

    /**
     * The first method called after main() which loads up the things needed to show the loading screen.
     */
    @Override
    public void create() {
        FontManager.initialize();

        loadingBatch = new SpriteBatch();
        BitmapFont loadingFont = new BitmapFont(Gdx.files.internal("assets/fonts/inconsolata-32.fnt"));

        trousers = new Texture(Gdx.files.internal("assets/graphics/trousers.png"));
        pixel = new Texture(Gdx.files.internal("assets/graphics/pixel.png"));

        /* Sets up the post processor */
        ShaderLoader.BasePath = "assets/shaders/";
        postProcessor = new PostProcessor(false, false, true);

        /* Creates the CRTScreen effect */
        int effects = CrtScreen.Effect.Scanlines.v | CrtScreen.Effect.PhosphorVibrance.v;
        crtMonitor = new CrtMonitor(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false, CrtScreen.RgbMode.RgbShift, effects);
        crtMonitor.setColorOffset(0.001f);
        postProcessor.addEffect(crtMonitor);

        /* Creates the screen bulge effect */
        Curvature curvature = new Curvature();
        curvature.setDistortion(0.2f);
        postProcessor.addEffect(curvature);

        /* Creates the screen curvature effect */
        bloom = new Bloom(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bloom.setBloomIntesity(0.1f);
        postProcessor.addEffect(bloom);
    }

    /**
     * The actual resource-loading.
     * TODO: Find a way to make this asynchronous!
     */
    private void load() {
        /* Initializing the managers */
        SoundManager.initialize();
        Art.initialize();
        HelpSection.initialize();
        RaceManager.initialize();
        SettingsManager.initialize();

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
        screen = new StartScreen(this);
        screenChangeJitter = 10;

        //  Plays the initial epic music
        SoundManager.playMusic("" + Tools.decide("spacerace"), false);
    }

    private void update(float dt) {
        age += Gdx.graphics.getDeltaTime();

        if (!firstFrame) {
            /* We will update the background stars if we are not on the play screen */
            if (!(screen instanceof PlayScreen))
                for (BackgroundElement element : backgroundElements) {
                    element.update(Gdx.graphics.getDeltaTime());
                }

            /* Updating relevant objects */
            screen.update(Gdx.graphics.getDeltaTime());

            Notification.update(Gdx.graphics.getDeltaTime());
            SoundManager.update();
            if (screenChangeJitter > 0.001f) {
                screenChangeJitter -= Gdx.graphics.getDeltaTime() * 5.f;
                bloom.setBloomIntesity(10);
            } else {
                screenChangeJitter = 0;
                bloom.setBloomIntesity(0.1f);
            }
        }
    }

    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());

        if (exiting) return;

        /* Clears the screen for a render */
        Gdx.gl20.glClearColor(0, 0, 0, 1f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        postProcessor.setEnabled(SettingsManager.isPostprocessing());

        if (postProcessor.isEnabled()) {
            crtMonitor.setTime(age);
            float f = MathUtils.random(screenChangeJitter);
            crtMonitor.setColorOffset(0.00175f + f);
            // Captures screen for post processing
            postProcessor.capture();
        }

        if (firstFrame) {
            loadingBatch.begin();
            loadingBatch.draw(trousers, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() * 0.5f, 100, 100);

            float height = 50;
            for (int f = 0; f < height; f++) {
                loadingBatch.setColor(new Color(1, -0.025f * f + 0.5f, 1 / (f + 1), ((height - f) / height)));
                for (int g = 0; g < 25; g++) {
                    if (MathUtils.randomBoolean(((height - f) / height)))
                        loadingBatch.draw(pixel, Gdx.graphics.getWidth() / 2 - 24 + g * 2, Gdx.graphics.getHeight() * 0.5f + 100 + f * 2, 2, 2);
                }
            }
            loadingBatch.setColor(Color.WHITE);

            FontManager.getFont("inconsolata-36").draw(loadingBatch, "l o a d i n g . . .", 0, Gdx.graphics.getHeight() * 0.4f, Gdx.graphics.getWidth(), Align.center, false);
            loadingBatch.end();

                /* Only loads a second in, allowing the loading message to show properly */
            if (age > 1) {
                load();
                firstFrame = false;
            }
        } else {

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
        }

        // Finally (Part 2) we render the post-processed screen if we need to
        if (postProcessor.isEnabled()) {
            postProcessor.render();
        }
    }

    @Override
    public void dispose() {
        if (exiting) return;
        super.dispose();
        Gdx.app.log("RaceGame", "Disposing of things...");
        screen.dispose();
        backgroundRenderer.dispose();
        postProcessor.dispose();
        Notification.dispose();
        Gdx.app.log("RaceGame", "Done!");
        exiting = true;
        Gdx.app.exit();
    }

    /**
     * Sets the screen to the desired one.
     */
    public void changeScreen(String name) {
        /* Get rid of the current screen */
        System.out.println("Sert");
        screen.dispose();

        /* Adds to the color jitter effect when changing screens */
        screenChangeJitter = 0.1f;

        /* Sets the screen accordingly */
        switch (name) {
            case "play":
                screen = new PlayScreen(this, false);
                SoundManager.stopMusic("menu");
                SoundManager.stopMusic("spacerace");
                break;
            case "multiplay":
                screen = new PlayScreen(this, true);
                SoundManager.stopMusic("menu");
                SoundManager.stopMusic("spacerace");
                break;
            case "menu":
                /* Welcomes the player if it is their first time playing */
                if (SettingsManager.isFirstTime()) {
                    Notification.show("Welcome to SpaceRace!\nDespite its cute looks, this game is quite literally rocket science and thus the help section is highly recommended.", "Take Me There", "I'm Smart", Colors.P_PINK, Colors.P_BLUE, () -> {
                        changeScreen("help");
                        Notification.showing = false;
                    }, () -> Notification.showing = false);
                    SettingsManager.setFirstTime(false);
                    SettingsManager.writeSettings();
                }
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
        if (!name.equals("play")) {
            SoundManager.stopMusic("oxidiser");
            SoundManager.stopMusic("alephnull");

            if (!SoundManager.isMusicPlaying("menu") && !SoundManager.isMusicPlaying("spacerace"))
                SoundManager.playMusic("menu", true);
        }

        //  Changes the direction of the motions of the background images
        BackgroundElement.d += 90;
        BackgroundElement.d %= 360;
    }

    public void changeResolution() {
        //  The Notification's buttons need to be repositioned due to the resolution change
        Notification.resetValues();
        backgroundRenderer.dispose();
        backgroundRenderer = new ShapeRenderer();

        for (BackgroundElement backgroundElement : backgroundElements) {
            backgroundElement.x = MathUtils.random(Gdx.graphics.getWidth());
            backgroundElement.y = MathUtils.random(Gdx.graphics.getHeight());
        }
    }
}

/**
 * A class to define the little stars and planets you see in the menu background
 */
class BackgroundElement {
    static float d;
    float x;
    float y;
    float radius;
    Color color;
    float v;

    BackgroundElement() {
        x = MathUtils.random(Gdx.graphics.getWidth());
        y = MathUtils.random(Gdx.graphics.getHeight());

        if (MathUtils.randomBoolean(0.001f)) {
            color = new Color(Colors.getRandomPlanetColor()).mul(0.7f);
            radius = MathUtils.random(5, 20);
        } else {
            float e = MathUtils.random() * 0.7f;
            color = new Color(e, e, e, 1);
            radius = MathUtils.random(0.5f, 1.1f);
        }
        v = MathUtils.random(10) + 15;
    }

    private void reset() {
        v = MathUtils.random(10) + 15;
        if (d == 0) x = -radius;
        if (d == 90) y = -radius;
        if (d == 180) x = Gdx.graphics.getWidth() + radius;
        if (d == 270) y = Gdx.graphics.getHeight() - radius;

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
        x += v * MathUtils.cosDeg(d) * dt;
        y += v * MathUtils.sinDeg(d) * dt;

        if (d == 0 && x > Gdx.graphics.getWidth() + radius) reset();
        if (d == 90 && y > Gdx.graphics.getHeight() + radius) reset();
        if (d == 180 && x < -radius) reset();
        if (d == 270 && y < -radius) reset();
    }
}