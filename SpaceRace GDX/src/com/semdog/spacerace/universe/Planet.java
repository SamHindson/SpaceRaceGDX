package com.semdog.spacerace.universe;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.graphics.Colors;

public class Planet {
	
	private float radius, x, y;
	private float mass;
	
	private Color color;
	
	private Random dustMaker;
	private int dustBalls;
	private float[] ballX, ballY, ballR;
	private Color[] dustColors;
	
	public Planet(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		
		mass = MathUtils.PI * radius * radius;
		
		System.out.println("Planet mass: " + mass);
		System.out.println("Planet Gravity at Surface: " + getGravity(radius) + "m/s2");
		
		color = Colors.getRandom();
		
		dustMaker = new Random((int)(x + y + radius));
		dustBalls = dustMaker.nextInt(100);
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
		shapeRenderer.setColor(color);
		shapeRenderer.circle(x, y, radius, 100);
		
		for(int h = 0; h < dustBalls; h++) {
			shapeRenderer.setColor(dustColors[h]);
			shapeRenderer.circle(ballX[h], ballY[h], ballR[h]);
		}
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

	public Color getColor() {
		return color;
	}
}
