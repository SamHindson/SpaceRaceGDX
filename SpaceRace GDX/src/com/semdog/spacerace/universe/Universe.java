package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.players.Player;

public class Universe {
	
	public static final float GRAVITY = 50f;

	private Array<Planet> planets;
	private Array<Mass> masses;
	
	private Player player;

	private SpriteBatch universeBatch;
	
	private OrthographicCamera camera;

	public Universe() {
		planets = new Array<>();
		masses = new Array<>();

		planets.add(new Planet(0, 0, 500));
		
		player = new Player(0, 600, planets.get(0));
		
		universeBatch = new SpriteBatch();
		
		camera = new OrthographicCamera(800, 600);
		camera.position.set(0, 0, 0);
		camera.zoom = 0.5f;
		
		universeBatch.setProjectionMatrix(camera.combined);
		
		camera.update();
	}

	public void tick(float dt) {
		for(Mass mass : masses) {
			mass.update(dt, planets);
		}
		
		camera.zoom = 0.3f;

		player.update(dt);
		camera.position.set(player.getX(), player.getY(), 0);
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
		player.draw(universeBatch);
		universeBatch.end();
	}

}
