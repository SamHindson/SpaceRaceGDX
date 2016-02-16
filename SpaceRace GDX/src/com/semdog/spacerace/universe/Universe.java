package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Universe {

	private Array<Planet> planets;
	private Array<Mass> masses;

	private SpriteBatch universeBatch;

	public Universe() {
		planets = new Array<>();
		masses = new Array<>();

		planets.add(new Planet(0, 0, 1000));
		
		universeBatch = new SpriteBatch();
	}

	public void tick(float dt) {

	}

	public void render() {
		universeBatch.begin();
		for (Planet planet : planets) {
			planet.draw(universeBatch);
		}
		universeBatch.end();
	}

}
