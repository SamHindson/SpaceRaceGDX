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

	private float x, y;
	private float dd;

	public Player(float x, float y, Planet planet) {
		environment = planet;
		distance = Vector2.dst(x, y, planet.getX(), planet.getY());
		angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());

		this.x = x;
		this.y = y;

		sprite = new Sprite(new Texture(Gdx.files.internal("assets/dude.png")));
		sprite.setSize(20, 20);

	}

	public void update(float dt) {
		if (!onGround) {
			if (distance > environment.getRadius() + 10) {
				onGround = false;
			} else {
				onGround = true;
			}
		}

		if (onGround) {
			distance = environment.getRadius() + 10;
			dd = 0;
		} else {
			dd += -environment.getGravity(distance) * dt;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			angle += dt * 50 / distance;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			angle -= dt * 50 / distance;
		}
		
		if(Gdx.input.isKeyPressed(Keys.SPACE) && onGround) {
			dd += 100;
			onGround = false;
		}
		
		distance += dd * dt;

		System.out.println(dd);
		
		sprite.rotate(angle * MathUtils.radiansToDegrees);

		x = environment.getX() + distance * MathUtils.cos(angle);
		y = environment.getY() + distance * MathUtils.sin(angle);

		sprite.setPosition(x - 10, y - 10);
		distance = Vector2.dst(x, y, environment.getX(), environment.getY());
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
