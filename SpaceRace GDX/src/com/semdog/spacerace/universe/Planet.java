package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Planet {
	
	private float radius, x, y;
	private float mass;
	
	private Texture texture;
	
	public Planet(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		
		mass = MathUtils.PI * radius * radius;
		
		Pixmap pixmap = new Pixmap((int)(radius * 2), (int)(radius * 2), Format.RGBA4444);
		pixmap.setColor(Color.RED.lerp(Color.WHITE, 0.1f));
		pixmap.fillCircle(0, 0, (int)radius);
		
		texture = new Texture(pixmap);
	}
	
	public void draw(SpriteBatch batch) {
		batch.draw(texture, x, y);
	}
}
