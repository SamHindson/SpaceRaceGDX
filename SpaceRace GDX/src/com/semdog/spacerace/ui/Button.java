package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.semdog.spacerace.graphics.Colors;

public class Button {
	private static BitmapFont font16, font10;
	private static Texture texture;

	static {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		parameter.size = 16;
		font16 = generator.generateFont(parameter);

		parameter.size = 12;
		font10 = generator.generateFont(parameter);
		generator.dispose();

		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		texture = new Texture(pixmap);
	}

	private float x, y, w, h, tox, toy;
	private Event clickEvent;
	private boolean hovered, held;

	private Color buttonColor, textColor;
	private boolean darkHover;
	private String text;

	private boolean small = false;
	
	private GlyphLayout glyphLayout;

	public Button(String text, boolean small, float x, float y, float w, float h, Event clickEvent) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.clickEvent = clickEvent;
		this.text = text;

		this.small = small;

		glyphLayout = null;

		if (small) {
			glyphLayout = new GlyphLayout(font10, text);
			tox = 0;
			toy = 0;
		} else {
			glyphLayout = new GlyphLayout(font16, text);
			tox = 0;
			toy = 0;
		}
		
		tox = -glyphLayout.width / 2;
		toy = glyphLayout.height / 2;

		buttonColor = Color.WHITE;
		textColor = Color.BLACK;
	}

	public void setColors(Color buttonColor, Color textColor) {
		this.buttonColor = buttonColor;
		this.textColor = textColor;
		darkHover = (buttonColor.r + buttonColor.g + buttonColor.b) < 1.5f;
	}

	public void update(float dt) {
		if (Gdx.input.getX() > x - w / 2 && Gdx.input.getX() < x + w / 2
				&& Gdx.graphics.getHeight() - Gdx.input.getY() > y - h / 2
				&& Gdx.graphics.getHeight() - Gdx.input.getY() < y + h / 2) {
			hovered = true;

			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				held = true;
			}
		} else {
			hovered = false;
		}

		if (held && !Gdx.input.isButtonPressed(Buttons.LEFT)) {
			clickEvent.execute();
			held = false;
		}
	}

	public void draw(SpriteBatch batch) {
		batch.setColor(hovered ? textColor : buttonColor);
		batch.draw(texture, x - w / 2, y - h / 2, w, h);

		batch.setColor(Color.WHITE);
		if (small) {
			font10.setColor(hovered ? buttonColor : textColor);
			font10.draw(batch, text, x + tox, y + toy);
		} else {
			font16.setColor(hovered ? buttonColor : textColor);
			font16.draw(batch, text, x + tox, y + toy);
		}
		
		/*batch.setColor(Colors.PLANETORANGE);
		batch.draw(texture, x - 2, y - 2, 4, 4);*/
	}
}
