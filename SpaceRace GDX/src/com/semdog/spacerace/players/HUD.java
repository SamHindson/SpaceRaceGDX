package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.universe.Universe;

/**
 * Created by sam on 2016/05/01.
 * <p>
 * The Heads Up Display seen by players.
 * Shows them all sorts of useful information, such as if they are busy bleeding out
 */

public class HUD {
    private Player owner;
    private String title, subtitle;
    private boolean messageShowing = false;
    private boolean respawning = false, respawnable = false;
    private float respawnCounter;

    private BitmapFont titleFont, subtitleFont;
    private float titleX, titleY, subtitleX, subtitleY, respawnX, respawnY;

    public HUD(Player owner) {
        this.owner = owner;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/VCR_OSD_MONO_1.001.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        titleFont = generator.generateFont(parameter);
        parameter.size = 32;
        subtitleFont = generator.generateFont(parameter);
        generator.dispose();

        GlyphLayout resGlyph = new GlyphLayout(subtitleFont, "PRESS [E] TO BOARD");
        float p = resGlyph.width / 2.f;
        float o = resGlyph.height / 2.f;

        System.out.println(1238 + 45555);
        System.out.println(p + ", " + o);
    }

    public void update(float dt) {
        if (respawning) {
            respawnCounter -= dt;

            if (respawnCounter <= 0) {
                respawning = false;

                messageShowing = false;

                if (respawnable)
                    Universe.currentUniverse.respawnPlayer();
            }
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        if (messageShowing) {
            titleFont.draw(spriteBatch, title, titleX, titleY);

            subtitleFont.setColor(Color.WHITE);
            subtitleFont.draw(spriteBatch, subtitle, subtitleX, subtitleY);
        }

        if (respawning) {
            subtitleFont.setColor(Color.WHITE);
            subtitleFont.draw(spriteBatch, "[" + (int) (respawnCounter) + "]", respawnX, respawnY);
        }

        if (owner.isBoarding()) {
            subtitleFont.setColor(new Color((int) (MathUtils.random() * Integer.MAX_VALUE)));
            subtitleFont.draw(spriteBatch, "PRESS {E} TO BOARD", Gdx.graphics.getWidth() / 2.f - 169.0f, Gdx.graphics.getHeight() * 0.4f - 10.5f);
        }
    }

    public void setDead(DeathCause reason, boolean respawnable) {
        this.respawnable = respawnable;

        if (respawnable) {
            respawning = true;
            respawnCounter = 6;
        }

        setText(getDeathDesc(), reason.getDetails());
    }

    private String getDeathDesc() {
        String[] descs = {
                "You're dead now",
                "You're dead.",
                "You're with the Lord now.",
                "You're morsdood",
                "You've been murdered",
                "You got got",
                "Rekt."
        };
        return (String) Tools.decide(descs);
    }

    private void setText(String title, String subtitle) {
        messageShowing = true;
        this.title = title;
        this.subtitle = subtitle;

        GlyphLayout titGlyph = new GlyphLayout(titleFont, title);
        titleX = Gdx.graphics.getWidth() / 2 - titGlyph.width / 2.f;
        titleY = Gdx.graphics.getHeight() * 0.6f - titGlyph.height / 2.f;

        GlyphLayout subGlyph = new GlyphLayout(subtitleFont, subtitle);
        subtitleX = Gdx.graphics.getWidth() / 2 - subGlyph.width / 2.f;
        subtitleY = Gdx.graphics.getHeight() * 0.5f - subGlyph.height / 2.f;

        GlyphLayout resGlyph = new GlyphLayout(subtitleFont, "[0]");
        respawnX = Gdx.graphics.getWidth() / 2 - resGlyph.width / 2.f;
        respawnY = Gdx.graphics.getHeight() * 0.35f - resGlyph.height / 2.f;
    }
}
