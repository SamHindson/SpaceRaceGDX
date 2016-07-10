package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.semdog.spacerace.graphics.Art;

public class Button {
	private static BitmapFont font16, font10;

	static {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		parameter.size = 16;
		font16 = generator.generateFont(parameter);

		parameter.size = 12;
		font10 = generator.generateFont(parameter);
		generator.dispose();
	}

    private float x, y, w, h;
    private Event clickEvent;
	private boolean hovered, held;

	private Color buttonColor, textColor;
    private String text;

	private boolean small = false;
    private boolean notification = false;

    public Button(String text, boolean small, float x, float y, float w, float h, Event clickEvent) {
        this(text, small, x, y, w, h, false, clickEvent);
    }

    public Button(String text, boolean small, float x, float y, float w, float h, boolean notification, Event clickEvent) {
        this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.clickEvent = clickEvent;
		this.text = text;

		this.small = small;

        this.notification = notification;

		buttonColor = Color.WHITE;
		textColor = Color.BLACK;
	}

	public void setColors(Color buttonColor, Color textColor) {
		this.buttonColor = buttonColor;
		this.textColor = textColor;
	}

	public void update(float dt) {
        if (Notification.showing && !notification)
            return;

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
        //batch.setColor(hovered ? buttonColor : textColor);
        batch.setColor(hovered ? textColor : buttonColor);
        batch.draw(Art.get("pixel_white"), x - w / 2, y - h / 2, w, h);
        //batch.draw(texture, x - w / 2 + 2, y - h / 2 + 2, w - 4, h - 4);

		batch.setColor(Color.WHITE);

		if (small) {
			font10.setColor(hovered ? buttonColor : textColor);
            font10.draw(batch, text, x - w / 2, y + font10.getLineHeight() / 4, w, 1, false);
        } else {
			font16.setColor(hovered ? buttonColor : textColor);
            font16.draw(batch, text, x - w / 2, y + font16.getLineHeight() / 4, w, 1, false);
        }
	}

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEvent(Event event) {
        this.clickEvent = event;
    }
}
