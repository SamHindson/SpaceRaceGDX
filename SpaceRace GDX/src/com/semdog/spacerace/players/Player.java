package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.universe.Planet;

public class Player {

	private Planet environment;
	private float distance, angle;

	private boolean onGround = false;

	private Sprite sprite;

	private float x, y, a;

	public Player(float x, float y, Planet planet) {
		environment = planet;
		distance = Vector2.dst(x, y, planet.getX(), planet.getY());
		angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());

		this.x = x;
		this.y = y;

		sprite = new Sprite(new Texture(Gdx.files.internal("assets/test.png")));
		sprite.setSize(5, 5);
	}

	public void update(float dt) {
		distance = Vector2.dst(x, y, environment.getX(), environment.getY());

		if (!onGround) {
			if (distance > environment.getRadius()) {
				onGround = false;
			} else {
				onGround = true;
			}
		}

		if (onGround) {
			distance = environment.getRadius();
			a = 0;
		} else {
			a = -environment.getGravity(distance) * dt;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			angle += dt * 0.2f;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			angle -= dt * 0.2f;
		}
		
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			a = 10;
		}
		
		distance += a * dt;

		System.out.println(a);

		x = environment.getX() + distance * MathUtils.cos(angle);
		y = environment.getY() + distance * MathUtils.sin(angle);

		sprite.setPosition(x - 2.5f, y - 2.5f);
	}

	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

}
