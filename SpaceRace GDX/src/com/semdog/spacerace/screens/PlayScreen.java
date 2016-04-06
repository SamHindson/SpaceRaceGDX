package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.universe.Universe;

public class PlayScreen extends RaceScreen {

	private Universe universe;
	private Thread physicsThread;

	public PlayScreen(RaceGame game) {
		super(game);

		universe = new Universe();
		physicsThread = new Thread(new PhysicsRunnable(), "SpaceRace Physics Thread");
		physicsThread.start();
	}

	@Override
	public void update(float dt) {
		universe.tick(dt);
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

	}

	class PhysicsRunnable implements Runnable {

		boolean running = false;

		@Override
		public void run() {
			running = true;

			while (running) {
				universe.tickPhysics(0.016f);
				universe.finalizeState();
				
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void stop() {
			running = false;
		}

	}

}
