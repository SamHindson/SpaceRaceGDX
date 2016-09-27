package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.screens.PlayScreen;

/**
 * An extension of the PauseMenu which also allows players to commit suicide.
 *
 * @author Sam
 */

public class MultiplayerPauseMenu extends Overlay {

    private BitmapFont titleFont;
    private Button resume, restart, exit;

    public MultiplayerPauseMenu(PlayScreen container) {
        titleFont = FontManager.getFont("mohave-84-italic");

        resume = new Button("Resume", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.4f + 50, 200, 50, () -> {
            showing = false;
            container.unpause();
        });

        restart = new Button("Respawn", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.4f, 200, 50, () -> {
            showing = false;
            container.unpause();
            container.killPlayer();
        });

        exit = new Button("Exit", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.4f - 50, 200, 50, () -> {
            showing = false;
            container.exit();
            container.unpause();
        });

        resume.setColors(Colors.UI_GREEN, Colors.UI_WHITE);
        restart.setColors(Colors.P_ORANGE, Colors.UI_WHITE);
        exit.setColors(Colors.UI_RED, Colors.UI_WHITE);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.setColor(0.f, 0.f, 0.f, 0.75f);
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        titleFont.draw(spriteBatch, "Not Really Paused", 0, Gdx.graphics.getHeight() * 0.75f, Gdx.graphics.getWidth(), 1, false);
        resume.draw(spriteBatch);
        restart.draw(spriteBatch);
        exit.draw(spriteBatch);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        resume.update(dt);
        restart.update(dt);
        exit.update(dt);
    }

}
