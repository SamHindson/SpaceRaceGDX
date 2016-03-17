package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.misc.Tools;

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
			float v = MathUtils.random(magnitude) / 2;
			float a = MathUtils.random(MathUtils.PI2);
			
			xs[j] = x;
			ys[j] = y;
			dxs[j] = v * MathUtils.cos(a);
			dys[j] = v * MathUtils.sin(a);
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
			batch.draw(Art.get("pixel_" + Tools.decide("red", "yellow", "orange")), xs[j], ys[j], (life - age) * 5, (life - age) * 5);
		}
	}

	public boolean alive() {
		return age < life;
	}
}
