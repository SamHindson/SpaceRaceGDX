package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;

/**
 * A class which allows messages to be displayed to the user which override any
 * other UI elements currently on screen.
 *
 * @author Sam
 */

public class Notification {

    public static boolean showing = false;
    private static float height, textHeight;
    private static boolean showingSingle;

    private static SpriteBatch batch;
    private static BitmapFont font;
    private static String title;
    private static Button one, two;

    static {
        one = new Button("One", false, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 10, Gdx.graphics.getWidth() / 6, 50, true, null);
        two = new Button("Two", false, Gdx.graphics.getWidth() / 2 + 100, Gdx.graphics.getHeight() / 2 - 10, Gdx.graphics.getWidth() / 6, 50, true, null);

        font = FontManager.getFont("inconsolata-28");

        batch = new SpriteBatch();
    }

    public static void show(String _title, String _textOne, String _textTwo, Color colorOne, Color colorTwo, Event _eventOne, Event _eventTwo) {
        title = _title;
        one.setText(_textOne);
        two.setText(_textTwo);
        one.setColors(colorOne, Colors.UI_WHITE);
        two.setColors(colorTwo, Colors.UI_WHITE);
        one.setEvent(_eventOne);
        two.setEvent(_eventTwo);

        GlyphLayout glyphLayout = new GlyphLayout(font, title, Colors.UI_WHITE, Gdx.graphics.getWidth() / 3 - 20, 0, true);
        textHeight = glyphLayout.height;
        height = textHeight + 40 + 50;

        one.setPosition(Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 12, Gdx.graphics.getHeight() / 2 - height / 2 - 25);
        two.setPosition(Gdx.graphics.getWidth() / 2 + Gdx.graphics.getWidth() / 12, Gdx.graphics.getHeight() / 2 - height / 2 - 25);

        one.setSize(Gdx.graphics.getWidth() / 6, 50);
        two.setSize(Gdx.graphics.getWidth() / 6, 50);

        showingSingle = false;
        showing = true;
    }

    public static void show(String _title, String _textOne, Color colorOne, Event _eventOne) {
        title = _title;
        one.setText(_textOne);
        one.setColors(colorOne, Colors.UI_WHITE);
        one.setEvent(_eventOne);

        GlyphLayout glyphLayout = new GlyphLayout(font, title);
        textHeight = glyphLayout.height;
        height = glyphLayout.height + 140;

        one.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - height / 2 - 25);
        one.setSize(Gdx.graphics.getWidth() / 3, 50);

        showingSingle = true;
        showing = true;
    }

    public static void update(float dt) {
        if (showing) {
            one.update(dt);

            if (!showingSingle)
                two.update(dt);
        }
    }

    public static void draw() {
        if (!showing)
            return;

        batch.begin();
        batch.setColor(0.f, 0.f, 0.f, 0.75f);
        batch.draw(Art.get("pixel_white"), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.DARK_GRAY);
        batch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 2 - height / 2, Gdx.graphics.getWidth() / 3, height);
        batch.setColor(Color.WHITE);
        font.setColor(Colors.UI_WHITE);
        font.draw(batch, title, Gdx.graphics.getWidth() / 3 + 10, Gdx.graphics.getHeight() / 2 + textHeight / 2, Gdx.graphics.getWidth() / 3 - 20, 1, true);
        one.draw(batch);

        if (!showingSingle)
            two.draw(batch);

        batch.end();
    }

    /**
     * In the case of a resolution change, the Notification's SpriteBatch needs
     * to be readjusted for the new dimensions. Other screens don't have this
     * problem because the SpriteBatches are remade when the screen changes.
     * <p>
     * This SpriteBatch is static, and thus needs a kick in the trousers for it
     * to recognize the changed resolution.
     */
    public static void resetValues() {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public static void dispose() {
        batch.dispose();
    }
}
