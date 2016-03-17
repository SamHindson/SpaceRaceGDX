package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.universe.Grenade;
import com.semdog.spacerace.universe.Planet;

public class Player {

	private Planet environment;
	private float distance, angle;

	private boolean onGround = false;

	private Sprite sprite;

	private float x, y;
	private float ax, ay, a;
	private float dd;
	
	private Rectangle bounds;

	public Player(float x, float y, Planet planet) {
		environment = planet;
		distance = Vector2.dst(x, y, planet.getX(), planet.getY());
		angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());

		this.x = x;
		this.y = y;

		sprite = new Sprite(new Texture(Gdx.files.internal("assets/dude.png")));
		sprite.setSize(20, 20);
		
		bounds = new Rectangle(x - 10, y - 10, 20, 20);
	}

	public void update(float dt, OrthographicCamera camera) {
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

		if (Gdx.input.isKeyPressed(Keys.A)) {
			angle += dt * 100 / distance;
		}

		if (Gdx.input.isKeyPressed(Keys.D)) {
			angle -= dt * 100 / distance;
		}

		if (Gdx.input.isKeyPressed(Keys.SPACE) && onGround) {
			dd += 100;
			onGround = false;
		}

		distance += dd * dt;

		sprite.setRotation(angle * MathUtils.radiansToDegrees - 90);

		x = environment.getX() + distance * MathUtils.cos(angle);
		y = environment.getY() + distance * MathUtils.sin(angle);
		
		bounds.setPosition(x - 10, y - 10);

		sprite.setPosition(x - 10, y - 10);
		distance = Vector2.dst(x, y, environment.getX(), environment.getY());

		// AIMING
		ax = Gdx.input.getX();
		ay = Gdx.input.getY();
		
		a = -MathUtils.atan2(ay - (Gdx.graphics.getHeight() / 2), ax - Gdx.graphics.getWidth() / 2);
		
		if(Gdx.input.isKeyJustPressed(Keys.G)) {
			System.out.println("New Granade!");
			float gx = x + 20 * MathUtils.cos(a);
			float gy = y + 20 * MathUtils.sin(a);
			
			float gdx = 375 * MathUtils.cos(a);
			float gdy = 375 * MathUtils.sin(a);
			
			new Grenade(gx, gy, gdx, gdy, 10, environment);
			System.out.println("?");
		}
	}

	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public void debugDraw(ShapeRenderer sr) {
		//sr.setColor(Color.BLUE);
		//sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void die(String string) {
		
	}

}
