package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.net.scoring.MetaPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * An overlay to show the multiplayer scores
 *
 * @author Sam
 */

public class Scores extends Overlay {

    private BitmapFont titleFont;
    private BitmapFont peopleFont;

    public Scores() {
        titleFont = FontManager.getFont("fipps-72");
        peopleFont = FontManager.getFont("mohave-40-bold");
    }

    public void update() {
        showing = true;
    }

    public void draw(SpriteBatch spriteBatch, HashMap<Integer, MetaPlayer> pinks, HashMap<Integer, MetaPlayer> blues) {
        showing = true;
        super.draw(spriteBatch);
        titleFont.setColor(Colors.UI_WHITE);
        titleFont.draw(spriteBatch, "Scores", 0, Gdx.graphics.getHeight() * 0.9f, Gdx.graphics.getWidth(), 1, false);

        float w = 0;
        for (Map.Entry<Integer, MetaPlayer> entry : pinks.entrySet()) {
            peopleFont.setColor(Colors.P_PINK);
            peopleFont.draw(spriteBatch, entry.getValue().getName(), 0, Gdx.graphics.getHeight() * 0.75f - peopleFont.getCapHeight() * w, Gdx.graphics.getWidth() * 0.45f, Align.right, false);
            peopleFont.setColor(Colors.P_WHITE);
            peopleFont.draw(spriteBatch, entry.getValue().getScore() + "", Gdx.graphics.getWidth() * 0.475f, Gdx.graphics.getHeight() * 0.75f - peopleFont.getCapHeight() * w, Gdx.graphics.getWidth() * 0.45f, Align.left, false);
            w++;
        }

        w = 0;
        for (Map.Entry<Integer, MetaPlayer> entry : blues.entrySet()) {
            peopleFont.setColor(Colors.P_BLUE);
            peopleFont.draw(spriteBatch, entry.getValue().getName(), Gdx.graphics.getWidth() * 0.55f, Gdx.graphics.getHeight() * 0.75f - peopleFont.getCapHeight() * w, Gdx.graphics.getWidth() * 0.45f, Align.left, false);
            peopleFont.setColor(Colors.P_WHITE);
            peopleFont.draw(spriteBatch, entry.getValue().getScore() + "", Gdx.graphics.getWidth() * 0.525f, Gdx.graphics.getHeight() * 0.75f - peopleFont.getCapHeight() * w, Gdx.graphics.getWidth() * 0.45f, Align.left, false);
            w++;
        }
    }
}
