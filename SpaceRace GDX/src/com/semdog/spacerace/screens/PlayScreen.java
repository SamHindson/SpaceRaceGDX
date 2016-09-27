package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.ui.Briefing;
import com.semdog.spacerace.ui.MultiplayerPauseMenu;
import com.semdog.spacerace.ui.Overlay;
import com.semdog.spacerace.ui.PauseMenu;
import com.semdog.spacerace.universe.MultiplayerUniverse;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.universe.UniverseLoader;

/**
 * The screen in which users play the actual game.
 *
 * @author Sam
 */

public class PlayScreen extends RaceScreen {

    private Universe universe;
    private Briefing briefing;
    private SpriteBatch overlayBatch;
    private Overlay pauseMenu;

    private boolean multiplayer = false;
    private boolean paused = false;
    private float timescale = 0;

    public PlayScreen(RaceGame game, boolean multiplayer) {
        super(game);

        overlayBatch = new SpriteBatch();

        this.multiplayer = multiplayer;

        if (multiplayer) {
            briefing = new Briefing(this, "Multiplayer Time!", "Welcome to SpaceRace Multiplayer.\nDon't even bother reporting the bugs; you'll be clicking 'Submit Another' for the rest of your days.\n\nThis is, at the moment, mostly a proof-of-concept kind of thing. So just appreciate how broken it is and move on.");
            pauseMenu = new MultiplayerPauseMenu(this);
        } else {
            briefing = new Briefing(this, RaceManager.getCurrentRace().getName(), RaceManager.getCurrentRace().getBriefing());
            pauseMenu = new PauseMenu(this);
        }

        bigBang(multiplayer);
    }

    @Override
    public void update(float dt) {
        if (briefing.isActive()) {
            briefing.update(dt);
        }

        if (paused) {
            universe.setPlayerEnabled(false);
            pauseMenu.update(dt);
        } else {
            universe.setPlayerEnabled(true);
        }

        universe.tick(timescale * dt);

        if (Universe.isExiting())
            return;

        universe.tickPhysics(timescale * dt);
        universe.finalizeState();

        if (Gdx.input.isKeyJustPressed(SettingsManager.getKey("PAUSE"))) {
            paused = !paused;
            if (!multiplayer) {
                universe.setActivated(!paused);
                timescale = paused ? 0 : 1;
            }
            pauseMenu.setShowing(paused);
        }
    }

    public void unpause() {
        paused = false;
        activate();
    }

    public void activate() {
        universe.setActivated(true);
        timescale = 1;
    }

    public void bigBang(boolean multiplayer) {
        if (universe != null)
            universe.dispose();

        timescale = 0;

        if (multiplayer) {
            universe = new MultiplayerUniverse(this, SettingsManager.getName());
        } else {
            universe = new Universe(this);
            new UniverseLoader().load(universe);
            universe.setActivated(false);
            SoundManager.playMusic(Tools.decide("oxidiser", "alephnull") + "", true);
        }

        briefing.setShowing(true);

    }

    @Override
    public void render() {
        universe.render();

        if (briefing.isActive()) {
            overlayBatch.begin();
            briefing.draw(overlayBatch);
            overlayBatch.end();
        }

        if (paused) {
            overlayBatch.begin();
            pauseMenu.draw(overlayBatch);
            overlayBatch.end();
        }
    }

    @Override
    public void exit() {
        if(multiplayer) {
            ((MultiplayerUniverse)universe).disconnect();
        }
        game.changeScreen("playmenu");
    }

    public void reset() {
        Universe.reset();

        paused = false;
        pauseMenu.setShowing(false);
    }

    public void killPlayer() {
        Universe.currentUniverse.playerKilled(DamageCause.RESPAWN);
    }
}
