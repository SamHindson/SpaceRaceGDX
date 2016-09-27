package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.ui.*;

/**
 * A screen which will help players struggling with the concepts of SpaceRace,
 * like me
 *
 * @author Sam
 */

public class HelpScreen extends RaceScreen implements ListViewListener {

    private SpriteBatch spriteBatch;
    private HelpSection helpSection;
    private ListView itemChooser;

    private TitleCard titleCard;
    private BitmapFont subtitleFont;

    private Button backButton, bugButton;

    public HelpScreen(RaceGame game) {
        super(game);

        titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);
        subtitleFont = FontManager.getFont("fipps-18");

        setTitle("Select a Help Topic!");

        spriteBatch = new SpriteBatch();
        helpSection = new HelpSection(350, Gdx.graphics.getHeight() * 0.1f, (Gdx.graphics.getWidth() - 100 - 250 - 100), Gdx.graphics.getHeight() * 0.7f);
        helpSection.setHelpID(0);
        itemChooser = new ListView(100, Gdx.graphics.getHeight() * 0.1f, 250, Gdx.graphics.getHeight() * 0.7f, Colors.UI_BLUE, Colors.UI_TEAL, 6);
        itemChooser.setTitles(helpSection.getTitles(), helpSection.getCompleted());
        itemChooser.setListener(this);

        backButton = new Button("Got it!", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.05f, 200, 50, this::exit);
        backButton.setColors(Colors.UI_YELLOW, Color.BLACK);

        bugButton = new Button("Report a Bug", false, (Gdx.graphics.getWidth() - 100 - 250 - 100) + 350 - 100, Gdx.graphics.getHeight() * 0.05f, 200, 50, () -> game.changeScreen("bug"));
        bugButton.setColors(Colors.UI_RED, Colors.UI_WHITE);
    }

    @Override
    public void update(float dt) {
        itemChooser.update(dt);
        helpSection.update(dt);
        backButton.update(dt);
        bugButton.update(dt);
    }

    @Override
    public void exit() {
        game.changeScreen("menu");
    }

    @Override
    public void render() {
        spriteBatch.begin();
        helpSection.draw(spriteBatch);
        itemChooser.draw(spriteBatch);
        drawTitle(spriteBatch);
        subtitleFont.setColor(Colors.P_GRAY);
        subtitleFont.draw(spriteBatch, "Help", 100, Gdx.graphics.getHeight() - 90);
        titleCard.draw(spriteBatch);
        backButton.draw(spriteBatch);
        bugButton.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void itemSelected(int index) {
        helpSection.setHelpID(index);
    }
}
