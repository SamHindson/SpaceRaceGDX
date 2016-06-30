package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.graphics.Colors;

import java.util.Random;

public class Planet {
	
	private Vector2 position;
	private float radius;
	private float mass;
	
	private String id;
	
	private Color color;
	
	private Random dustMaker;
	private int dustBalls;
	private float[] ballX, ballY, ballR;
	private Color[] dustColors;
	
	public Planet(String id, float x, float y, float radius) {
		position = new Vector2(x, y);
		this.radius = radius;
		
		this.id = id;
		
		mass = radius * radius * 5f;
		
		System.out.println("Planet mass: " + mass);
		System.out.println("Planet Gravity at Surface: " + getGravity(radius) + "m/s2");
		
		color = Colors.getRandom();
		
		dustMaker = new Random((int)(x + y + radius));
		dustBalls = dustMaker.nextInt(20);
		ballX = new float[dustBalls];
		ballY = new float[dustBalls];
		ballR = new float[dustBalls];
		dustColors = new Color[dustBalls];
		
		for(int j = 0; j < dustBalls; j++) {
			do {
				ballX[j] = MathUtils.random(-radius, radius);
				ballY[j] = MathUtils.random(-radius, radius);
				ballR[j] = MathUtils.random(10, 100);
			} while(Vector2.dst(0, 0, ballX[j], ballY[j]) + ballR[j] > radius);
			
			dustColors[j] = new Color(color.r + MathUtils.random(0.2f), color.g + MathUtils.random(0.2f), color.b + MathUtils.random(0.2f), 1.f);
		}
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		//shapeRenderer.set(ShapeType.Filled);
		shapeRenderer.setColor(color);
		shapeRenderer.circle(position.x, position.y, radius, 100);

		for(int h = 0; h < dustBalls; h++) {
            shapeRenderer.setColor(dustColors[h]);
			shapeRenderer.circle(ballX[h], ballY[h], ballR[h]);
		}
    }

	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
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

	public Color getColor() {
		return color;
	}

	public boolean inRange(float x2, float y2) {
		return Vector2.dst(position.x, position.y, x2, y2) < getSOI();
	}

	public String getID() {
		return id;
	}

	public Vector2 getPosition() {
		return position;
	}
}
