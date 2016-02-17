package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Universe {
	
	public static final int GRAVITY = 1;

	private Array<Planet> planets;
	private Array<Mass> masses;

	private SpriteBatch universeBatch;
	
	private OrthographicCamera camera;

	public Universe() {
		planets = new Array<>();
		masses = new Array<>();
		
		masses.add(new Mass(0, 750, 10));

		planets.add(new Planet(0, 0, 500));
		
		universeBatch = new SpriteBatch();
		
		camera = new OrthographicCamera(800, 600);
		camera.position.set(0, 0, 0);
		
		universeBatch.setProjectionMatrix(camera.combined);
		
		camera.update();
	}

	public void tick(float dt) {
		for(Mass mass : masses) {
			mass.update(dt, planets);
		}
		
		Mass m = masses.get(0);
		camera.position.set(m.getX(), m.getY(), 0);
		universeBatch.setProjectionMatrix(camera.combined);
		camera.update();
	}

	public void render() {
		universeBatch.begin();
		for (Planet planet : planets) {
			planet.draw(universeBatch);
		}
		for(Mass mass : masses) {
			mass.debugRender(universeBatch);
		}
		universeBatch.end();
	}

}
