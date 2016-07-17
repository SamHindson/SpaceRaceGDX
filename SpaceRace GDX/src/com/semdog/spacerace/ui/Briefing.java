package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.screens.PlayScreen;

/**
 * An overlay which shows the current race briefing in the PlayScreen.
 *
 * @author Sam
 */

public class Briefing extends Overlay {

    private boolean active = true;
    private String title, briefing;
    private Button go;

    private BitmapFont headingFont, titleFont, briefingFont;
    private PlayScreen container;

    public Briefing(PlayScreen container) {
        this(container, "???", "!?!?!");
    }

    public Briefing(PlayScreen container, String title, String briefing) {
        super();
        this.title = title;
        this.briefing = briefing;
        this.container = container;
        go = new Button("Go", false, Gdx.graphics.getWidth() / 2, 25, 150, 35, this::dismiss);
        go.setColors(Color.CLEAR, Colors.UI_GREEN);
        titleFont = FontManager.getFont("fipps-72-italic");
        briefingFont = FontManager.getFont("inconsolata-28");
        headingFont = FontManager.getFont("inconsolata-32-italic");
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        go.update(dt);
    }

    private void dismiss() {
        active = false;
        container.activate();
    }

    @Override
    public void setShowing(boolean showing) {
        super.setShowing(showing);
        active = true;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(0.f, 0.f, 0.f, 0.5f);
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        briefingFont.setColor(Colors.UI_WHITE);
        headingFont.draw(batch, "Mission Briefing:", 0, Gdx.graphics.getHeight() * 0.8f, Gdx.graphics.getWidth(), 1, false);
        titleFont.draw(batch, title, 0, Gdx.graphics.getHeight() * 0.8f - headingFont.getLineHeight() - 2, Gdx.graphics.getWidth(), 1, false);
        briefingFont.draw(batch, briefing, Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() * 0.8f - headingFont.getLineHeight() - titleFont.getCapHeight() - 40, Gdx.graphics.getWidth() / 3, 1, true);
        go.draw(batch);
    }

    public boolean isActive() {
        return active;
    }
}
