package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.TitleCard;

/**
 * Created by sam on 2016/09/24.
 */
public class StartScreen extends RaceScreen {
    private SpriteBatch batch;
    private BitmapFont font, subtitleFont;
    private TitleCard titleCard;
    private Button neatoButton;
    private float time;

    public StartScreen(RaceGame game) {
        super(game);

        font = FontManager.getFont("fipps-20");
        font.setColor(Colors.P_RED);
        subtitleFont = FontManager.getFont("inconsolata-18");
        batch = new SpriteBatch();
        titleCard = new TitleCard(TitleCard.BIG, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.65f);
    }


    @Override
    public void update(float dt) {
        time += dt;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) exit();
    }

    @Override
    public void render() {
        batch.begin();
        titleCard.draw(batch);
        subtitleFont.draw(batch, "(c) Flaming Trousers Studios 2016", 0, Gdx.graphics.getHeight() * 0.475f, Gdx.graphics.getWidth(), Align.center, false);
        if (MathUtils.sin(time * 5) > 0) {
            font.setColor(Colors.P_BLUE);
            font.draw(batch, "[PRESS ENTER]", 0, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth(), Align.center, false);
        }
        batch.end();
    }

    @Override
    public void exit() {
        game.changeScreen("menu");
    }
}
