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

public class Button {
	private static BitmapFont font;
	private static Texture texture;

	static {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;

		font = generator.generateFont(parameter);
		generator.dispose();
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		texture = new Texture(pixmap);
	}

	private float x, y, w, h, tx, ty;
	private Event clickEvent;
	private boolean hovered, held;

	private Color buttonColor, textColor;
	private boolean darkHover;
	private String text;

	public Button(String text, float x, float y, float w, float h, Event clickEvent) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.clickEvent = clickEvent;
		this.text = text;

		GlyphLayout glyphLayout = new GlyphLayout(font, text);
		tx = x - glyphLayout.width / 2.f;
		ty = y + glyphLayout.height / 2.f;

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
		font.setColor(hovered ? buttonColor : textColor);
		font.draw(batch, text, tx, ty);
	}
}
