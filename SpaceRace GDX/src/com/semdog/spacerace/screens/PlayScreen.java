package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.GameLoader;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.ui.Briefing;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.universe.UniverseLoader;

public class PlayScreen extends RaceScreen {

	private Universe universe;
	private Briefing briefing;
	private SpriteBatch briefBatch;

	private float timescale = 0.05f;
	private boolean shownBrief = false;

	public PlayScreen(RaceGame game) {
		super(game);
		
		Gdx.app.log("PlayScreen", "New PlayScreen, yo");

		briefBatch = new SpriteBatch();
		briefing = new Briefing(this, RaceManager.getCurrentRace().getName(),
				RaceManager.getCurrentRace().getBriefing());

		bigBang();
	}

	@Override
	public void update(float dt) {
		if (briefing.isActive()) {
			briefing.update(dt);
		}

		universe.tick(timescale * dt);
		
		if(Universe.isExiting())
			return;
		
		universe.tickPhysics(timescale * dt);
		universe.finalizeState();

		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			game.changeScreen("play");
	}

	public void activate() {
		universe.setActivated(true);
		timescale = 1;
		shownBrief = true;
	}

	public void bigBang() {
		universe = new Universe(this);
		new UniverseLoader().load(universe);

		SoundManager.playMusic("oxidiser", true);
		
		if(shownBrief)
			universe.setActivated(true);
	}

	@Override
	public void render() {
		universe.render();

		if (briefing.isActive()) {
			briefBatch.begin();
			briefing.draw(briefBatch);
			briefBatch.end();
		}
	}

}
