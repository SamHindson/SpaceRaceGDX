package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.universe.UniverseLoader;

public class PlayScreen extends RaceScreen {

	private Universe universe;
	//private Thread physicsThread;
	//private PhysicsRunnable runnable;

	public PlayScreen(RaceGame game) {
		super(game);

		universe = new Universe();
		new UniverseLoader().load(universe);
		//runnable = new PhysicsRunnable();
		//physicsThread = new Thread(runnable, "SpaceRace! Physics Thread");
		//physicsThread.start();
	}

	@Override
	public void update(float dt) {
		float t = 1;
		
		if (!universe.isLoading()) {
			universe.tick(t * dt);
			universe.tickPhysics(t * dt);
			universe.finalizeState();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			game.changeScreen("play");
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		Gdx.gl20.glClearColor(0, 0, 0, 0.9f);
		universe.render();
	}

	@Override
	public void dispose() {
		//runnable.stop();
		universe.dispose();
		System.out.println("Done disposiong");
	}

	private class PhysicsRunnable implements Runnable {

		boolean running = false;

		@Override
		public void run() {
			running = true;
			
			long lastTime = System.currentTimeMillis();
			long thisTime = System.currentTimeMillis();
			float dt = 0;

			while (running) {
				if (!universe.isLoading()) {
					thisTime = System.currentTimeMillis();
					dt = (thisTime - lastTime) / 1000.f;
					universe.tickPhysics(dt);
					universe.finalizeState();
					lastTime = thisTime;

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
