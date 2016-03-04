package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.GameLoader;

public class Planet {
	
	private float radius, x, y, r;
	private float mass;
	
	public Planet(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		
		mass = MathUtils.PI * radius * radius;
		
		System.out.println("Planet mass: " + mass);
		System.out.println("Planet Gravity at Surface: " + getGravity(radius) + "m/s2");
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(new Color(0.69f, 0.219f, 0.219f, 1f));
		shapeRenderer.circle(x, y, radius, 100);
	}

	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getSOI() {
		return radius * 5;
	}

	public float getMass() {
		return mass;
	}

	public float getRadius() {
		return radius;
	}
	
	public float getGravity(float distance) {
		return ((mass) * (Universe.GRAVITY)) / (float)Math.pow(distance, 2);
	}
}
