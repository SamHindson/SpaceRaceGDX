package com.semdog.spacerace.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;

/**
 * A UI element which can display textures all in a line, much like a museum
 * exhibit.
 * Used extensively in the Help Section.
 * 
 * @author Sam
 */

public class Exhibit {
	private String[] textures;
	private float width, height;
	private float padding;
	private float itemWidth;

	public Exhibit(float width, float height, String... textures) {
		this.textures = textures;
		this.width = width;
		this.height = height;

		padding = height * 0.1f;

		itemWidth = height - 2 * padding;
	}

	public void draw(SpriteBatch batch, float x, float y) {
		itemWidth = height - 4 * padding;
		float k = width / (textures.length + 1);
		float silSize = itemWidth * 1.1f;
		float silOffs = itemWidth * 0.05f;
		for (int e = 0; e < textures.length; e++) {
			batch.setColor(Colors.UI_YELLOW);
			batch.draw(Art.get(textures[e] + "_sil"), x + k * (e + 1) - itemWidth / 2 - silOffs,
					y + padding * 2 - silOffs, silSize, silSize);
			batch.setColor(Color.WHITE);
			batch.draw(Art.get(textures[e]), x + k * (e + 1) - itemWidth / 2, y + padding * 2, itemWidth, itemWidth);
		}
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}
}
