package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.TitleCard;

/**
 * A little screen that can be accessed by finding the secret button on the Main Menu where I (the developer) can
 * thank players for playing SpaceRace.
 */

public class ThankYouScreen extends RaceScreen {

    private SpriteBatch batch;
    private TitleCard titleCard;
    private Button neatoButton;
    private BitmapFont font;
    private Texture devPlanet;

    public ThankYouScreen(RaceGame game) {
        super(game);

        font = FontManager.getFont("inconsolata-28");
        batch = new SpriteBatch();
        titleCard = new TitleCard(TitleCard.BIG, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.85f);
        devPlanet = new Texture(Gdx.files.internal("assets/thanks.png"));

        neatoButton = new Button("Neato!", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.1f, 150, 75, () -> game.changeScreen("menu"));
        neatoButton.setColors(Colors.P_BLUE, Colors.UI_WHITE);
    }

    @Override
    public void update(float dt) {
        neatoButton.update(dt);
    }

    @Override
    public void render() {
        batch.begin();
        titleCard.draw(batch);
        neatoButton.draw(batch);
        font.setColor(Colors.UI_WHITE);
        String thankYou = "Developed by Sam Hindson\nCopyright Flaming Trousers Studios 2016\n\nMany thanks to my brave beta testers:\nBlake Denham\nNina Simon\nJon Taylor\nPaul Stansell\n\nThank you for playing! :)";
        font.draw(batch, thankYou, Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.6f, Gdx.graphics.getWidth() * 0.5f, 1, true);
        batch.draw(devPlanet, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void exit() {
        game.changeScreen("menu");
    }
}
