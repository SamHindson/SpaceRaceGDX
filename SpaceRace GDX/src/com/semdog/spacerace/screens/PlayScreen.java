package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.universe.UniverseLoader;

public class PlayScreen extends RaceScreen {

	private Universe universe;
	private Thread physicsThread;
	private PhysicsRunnable runnable;

	public PlayScreen(RaceGame game) {
		super(game);

		universe = new Universe();
		new UniverseLoader().load(universe);
		runnable = new PhysicsRunnable();
		physicsThread = new Thread(runnable, "SpaceRace! Physics Thread");
		physicsThread.start();
	}

	@Override
	public void update(float dt) {
		if (!universe.isLoading()) {
			universe.tick(dt);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		Gdx.gl20.glClearColor(0, 0, 0, 1f);
		universe.render();
	}

	@Override
	public void dispose() {
		runnable.stop();
		System.out.println("ENT");
	}

	private class PhysicsRunnable implements Runnable {

		boolean running = false;

		@Override
		public void run() {
			running = true;

			while (running) {
				if (!universe.isLoading()) {
					universe.tickPhysics(0.016f);
					universe.finalizeState();

					try {
						Thread.sleep(16);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					Gdx.app.log("PhysicsRunnable", "Universe is still loading!");
				}
			}
		}

		public void stop() {
			running = false;
		}

	}

}
