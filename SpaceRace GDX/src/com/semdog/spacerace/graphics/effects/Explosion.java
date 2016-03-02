package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.graphics.Art;

public class Explosion {
	private float x, y;
	private int magnitude;

	private float life = 1.5f;
	private float age = 0;
	
	private int particleNumber = 1000;
	
	private float[] xs;
	private float[] ys;
	private float[] dxs;
	private float[] dys;

	public Explosion(float x, float y, int magnitude) {
		this.x = x;
		this.y = y;
		this.magnitude = magnitude;
		
		xs = new float[particleNumber];
		ys = new float[particleNumber];
		dxs = new float[particleNumber];
		dys = new float[particleNumber];
		
		for(int j = 0; j < particleNumber; j++) {
			float k = MathUtils.random(magnitude) - magnitude/2;
			float m = MathUtils.random(magnitude) - magnitude/2;
			
			System.out.println(k + ", " + m);
			
			xs[j] = x;
			ys[j] = y;
			dxs[j] = k;
			dys[j] = m;
		}
	}

	public void update(float dt) {
		age += dt;
		
		for(int j = 0; j < particleNumber; j++) {
			xs[j] += dxs[j] * dt;
			ys[j] += dys[j] * dt;
			
			dxs[j] /= 1.1f;
			dys[j] /= 1.1f;
		}
	}

	public void draw(SpriteBatch batch) {
		for(int j = 0; j < particleNumber; j++) {
			batch.draw(Art.get("pixel_red"), xs[j], ys[j], MathUtils.random(life - age) * 10, MathUtils.random(life - age) * 10);
		}
	}

	public boolean alive() {
		return age < life;
	}
}
