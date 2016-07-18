package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.misc.FontManager;

/**
 * A clickable UI entity that does something when clicked. Like a normal UI
 * button.
 */

public class Button {
    private float x, y, width, height;

    private boolean hovered, held;
    private boolean notification = false; // Whether the button is one which appears on the notification overlay.

    private String text;
    private Color buttonColor, textColor;
    private Event clickEvent;
    private BitmapFont font;

    public Button(String text, boolean small, float x, float y, float w, float h, Event clickEvent) {
        this(text, small, x, y, w, h, false, clickEvent);
    }

    public Button(String text, boolean small, float x, float y, float width, float height, boolean notification, Event clickEvent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.clickEvent = clickEvent;
        this.text = text;
        this.notification = notification;
        buttonColor = Color.WHITE;
        textColor = Color.BLACK;
        font = small ? FontManager.getFont("fipps-12") : FontManager.getFont("fipps-16");
    }

    public void update(float dt) {
        // We do not want buttons in the background of a notification to be clickable
        if (Notification.showing && !notification)
            return;

        if (Gdx.input.getX() > x - width / 2 && Gdx.input.getX() < x + width / 2 && Gdx.graphics.getHeight() - Gdx.input.getY() > y - height / 2 && Gdx.graphics.getHeight() - Gdx.input.getY() < y + height / 2) {
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
        batch.draw(Art.get("pixel_white"), x - width / 2, y - height / 2, width, height);

        batch.setColor(Color.WHITE);

        font.setColor(hovered ? (buttonColor.equals(Color.CLEAR) ? Color.BLACK : buttonColor) : textColor);
        font.draw(batch, text, x - width / 2, y + font.getLineHeight() / 4, width, 1, false);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColors(Color buttonColor, Color textColor) {
        this.buttonColor = buttonColor;
        this.textColor = textColor;
    }

    public void setEvent(Event event) {
        this.clickEvent = event;
    }
}