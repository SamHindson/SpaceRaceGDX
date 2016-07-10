package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;

/**
 * Created by Sam on 2016/07/09.
 */

public class Notification {
    public static boolean showing = false;

    private static boolean showingSingle;

    private static SpriteBatch batch;
    private static BitmapFont font;

    private static String title;
    private static Button one, two;
    private static Event eventOne, eventTwo;

    private static float width = 600, height, textHeight;

    static {
        one = new Button("One", false, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 10, 175, 50, true, eventOne);
        two = new Button("Two", false, Gdx.graphics.getWidth() / 2 + 100, Gdx.graphics.getHeight() / 2 - 10, 175, 50, true, eventTwo);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/OldSansBlack.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = generator.generateFont(parameter);
        generator.dispose();

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

        GlyphLayout glyphLayout = new GlyphLayout(font, title);
        textHeight = glyphLayout.height;
        height = glyphLayout.height + 140;

        one.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - height / 4);
        two.setPosition(Gdx.graphics.getWidth() / 2 + 100, Gdx.graphics.getHeight() / 2 - height / 4);

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

        one.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - height / 4);

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
        font.draw(batch, title, Gdx.graphics.getWidth() / 3 + 10, Gdx.graphics.getHeight() / 2 + 0 + textHeight + 110 - height / 2, Gdx.graphics.getWidth() / 3 - 20, 1, true);
        one.draw(batch);

        if (!showingSingle)
            two.draw(batch);

        batch.end();
    }

    public static void resetValues() {
        //TODO ???
    }
}
