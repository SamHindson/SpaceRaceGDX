package com.semdog.spacerace.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;

/**
 * A UI element which draws the title of the screen in the classic SpaceRace
 * font and color scheme. Can be big or small.
 * 
 * @author Sam
 */

public class TitleCard {

	public static final int BIG = 1, SMALL = 0;
	
	private int size;
	private float x, y;
	private BitmapFont titleFont;

	public TitleCard(int size, float x, float y) {
		titleFont = size == SMALL ? FontManager.getFont("fipps-36") : FontManager.getFont("fipps-72");
		this.x = x;
		this.y = y;
		this.size = size;
	}

	public void draw(SpriteBatch batch) {
		if (size == 0) {
			titleFont.setColor(Colors.P_RED);
			titleFont.draw(batch, "Space", x, y);
			titleFont.setColor(Colors.P_YELLOW);
			titleFont.draw(batch, "Race!", x + 54, y - 40);
		} else {
			titleFont.setColor(Colors.P_RED);
			titleFont.draw(batch, "Space", x - 200, y + 50);
			titleFont.setColor(Colors.P_YELLOW);
			titleFont.draw(batch, "Race!", x - 92, y - 30);
		}
	}
}
