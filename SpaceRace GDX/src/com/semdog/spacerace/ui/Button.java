package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.misc.Tools;

/**
 * A clickable UI entity that does something when clicked. Like a normal UI button.
 *
 * @author Sam
 */

public class Button {
    private float x, y, width, height;

    private boolean hovered, held;
    private boolean notification = false; // Whether the button is one which appears on the notification overlay.
    private boolean beeps = true;
    private boolean borders = true;

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
        if (held) {
            if (!(Gdx.input.getX() > x - width / 2 && Gdx.input.getX() < x + width / 2 && Gdx.graphics.getHeight() - Gdx.input.getY() > y - height / 2 && Gdx.graphics.getHeight() - Gdx.input.getY() < y + height / 2)) {
                held = false;
            } else if (!Gdx.input.isButtonPressed(Buttons.LEFT)) {
                held = false;

                if (Gdx.input.getX() > x - width / 2 && Gdx.input.getX() < x + width / 2 && Gdx.graphics.getHeight() - Gdx.input.getY() > y - height / 2 && Gdx.graphics.getHeight() - Gdx.input.getY() < y + height / 2) {
                    clickEvent.execute();

                    if (beeps)
                        SoundManager.playSound("boop", 0.05f, 0);
                }
            }
        }
    }

    public void setBeeps(boolean beeps) {
        this.beeps = beeps;
    }

    public void setBorders(boolean borders) {
        this.borders = borders;
    }

    public void draw(SpriteBatch batch) {
        if (borders && !buttonColor.equals(Color.CLEAR)) {
            batch.setColor(held ? buttonColor.equals(Color.BLACK) ? Color.CLEAR : buttonColor : Tools.darker(buttonColor));
            batch.draw(Art.get("pixel_white"), x - width / 2, y - height / 2, width, height);
            //batch.setColor(hovered ? textColor : buttonColor.equals(Color.BLACK) ? Color.CLEAR : buttonColor);
            batch.setColor(held ? Tools.darker(buttonColor) : buttonColor.equals(Color.BLACK) ? Color.CLEAR : buttonColor);
            batch.draw(Art.get("pixel_white"), x - width / 2 + (hovered ? 2 : 0), y - height / 2 + (hovered ? 0 : 2), width - 2, height - 2);
        } else {
            batch.setColor(held ? textColor : buttonColor.equals(Color.BLACK) ? Color.CLEAR : buttonColor);
            batch.draw(Art.get("pixel_white"), x - width / 2, y - height / 2, width, height);
        }

        batch.setColor(Color.WHITE);

        //font.setColor(hovered ? (buttonColor.equals(Color.CLEAR) ? Color.BLACK : buttonColor) : textColor);
        font.setColor(held ? Color.WHITE : textColor);
        font.draw(batch, text, x - width / 2 - (borders ? held ? -1 : 1 : 0), y + font.getLineHeight() / 4 + (borders ? held ? -1 : 1 : 0), width, 1, false);
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