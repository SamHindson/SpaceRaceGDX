package com.semdog.spacerace.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Art;

/**
 * This class is going to be absolutely mental. A UI element which displays
 * things in a list. Much easier said than done.
 * 
 * @author Sam
 */

public class ListView {
	private Texture block;

	private int x, y, width, height;
	private int maxItems;

	private Color borderColor, itemColor;

	private Button up, down;
	private Button[] items;

	private int page = 0;
	private int pageCount;

	private String[] titles = { "Baby's First Rocket", "Into Orbit We Go", "To The Moooon!", "MURPH",
			"Quadroplanet Warfare", "SpaceDecathon!", "American Booty", "Wagonwheel?!" };

	public ListView(int x, int y, int width, int height, Color borderColor, Color itemColor, int maxItems) {
		block = Art.get("pixel_white");

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.maxItems = maxItems;

		this.borderColor = borderColor;
		this.itemColor = itemColor;

		up = new Button("^", true, x + width / 2, y + height - 25, width - 10, 40, () -> {
			if (page != 0)
				page--;

			recomposeItems();
		});
		up.setColors(borderColor, Color.WHITE);
		down = new Button("V", true, x + width / 2, y + 25, width - 10, 40, () -> {
			if (page != pageCount - 1)
				page++;

			recomposeItems();
		});
		down.setColors(borderColor, Color.WHITE);

		items = new Button[maxItems];
		pageCount = items.length / maxItems + 1;

		recomposeItems();
	}

	// TODO fix this stupid button placement
	private void recomposeItems() {
		int buttonHeight = (height - 111 + 2 * maxItems) / maxItems;
		int usableSpace = height - 10 - 80;

		items = new Button[maxItems];

		for (int k = 0; k < maxItems; k++) {

			int titleIndex = (k + maxItems * page);

			if (titleIndex - 2 > items.length)
				return;

			Button butt = new Button(titles[k + maxItems * page], true, x + width / 2,
					height - k * buttonHeight + 21 - 2 * k, width - 10 - 2, (usableSpace - 2 * (maxItems - 1)) / 5,
					() -> {
						itemChosen(titleIndex);
					});
			butt.setColors(itemColor, Color.WHITE);
			items[k] = butt;
		}
	}

	public void itemChosen(int index) {
		System.out.println(index);
	}

	public void update(float dt) {
		up.update(dt);
		down.update(dt);

		for (Button item : items) {
			if (item != null)
				item.update(dt);
		}
	}

	public void draw(SpriteBatch batch) {
		batch.setColor(borderColor);
		batch.draw(block, x, y, width, height);
		batch.setColor(Color.BLACK);
		batch.draw(block, x + 5, y + 5, width - 10, height - 10);
		up.draw(batch);
		down.draw(batch);

		for (Button item : items) {
			if (item != null)
				item.draw(batch);
		}
	}
}
