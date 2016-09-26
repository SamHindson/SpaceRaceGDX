package com.semdog.spacerace.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;

/**
 * A UI element which allows players to input text.
 *
 * @author Sam
 */

public class TextInput implements InputProcessor {

    private float x, y, width, height;
    private int limit = 250;
    private BitmapFont font;
    private String text = "";
    private String validChars = "";

    public TextInput(float x, float y, float width, float height, int limit) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.limit = limit;

        font = FontManager.getFont("inconsolata-24");
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        batch.draw(Art.get("pixel_white"), x, y, width, height);
        batch.setColor(Color.GRAY);
        batch.draw(Art.get("pixel_white"), x + 5, y + 5, width - 10, height - 10);

        font.setColor(Colors.UI_WHITE);
        font.draw(batch, text + "_", x + 10, y + height - 10, width - 20, 10, true);
        font.setColor(Colors.UI_BLUE);
        font.draw(batch, (limit - text.length() - 1) + "", x, y + 25, width - 10, 2, true);
    }

    public String getText() {
        return text;
    }

    public void setValidChars(String validChars) {
        this.validChars = validChars;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '\b' && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
            return false;
        }

        if (text.length() == limit - 1)
            return false;

        if (validChars.length() == 0 || validChars.contains(character + ""))
            text += character;
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void clear() {
        text = "";
    }
}
